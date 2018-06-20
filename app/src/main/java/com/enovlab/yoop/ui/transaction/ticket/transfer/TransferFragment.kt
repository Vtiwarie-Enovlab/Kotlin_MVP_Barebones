package com.enovlab.yoop.ui.transaction.ticket.transfer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.filter.search.adapter.SearchDecoration
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.data.entity.Contact
import com.enovlab.yoop.ui.transaction.ticket.transfer.contacts.ContactsAdapter
import com.enovlab.yoop.ui.widget.StatefulButton
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.*
import com.enovlab.yoop.utils.ext.postView
import com.enovlab.yoop.utils.ext.requiresContactsPermission
import com.enovlab.yoop.utils.ext.textChangeListener
import kotlinx.android.synthetic.main.fragment_ticket_details_transfer.*
import kotlinx.android.synthetic.main.layout_ticket_transfer_app_bar.*

class TransferFragment : TransactionFragment<TransferView, TransferViewModel>(), TransferView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = TransferViewModel::class.java

    private val contactsAdapter by lazy { ContactsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.ticketId = arguments!!.getString(ARG_TICKET_ID)
        viewModel.initialTransferState(TransferState.valueOf(arguments!!.getString(ARG_TRANSFER_STATE)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ticket_details_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transfer_back.setOnClickListener { viewModel.transferBack() }

        transfer_email_contacts.setOnClickListener {
            when {
                context!!.requiresContactsPermission() -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), RC_CONTACTS_PERMISSION)
                }
                else -> viewModel.useContacts()
            }
        }

        transfer_contacts_manual.setOnClickListener { viewModel.useManualInput() }

        transfer_contacts_input.textChangeListener(viewModel::searchContact)
        contactsAdapter.listener = viewModel::contactSelected
        transfer_contacts.adapter = contactsAdapter
        transfer_contacts.addItemDecoration(SearchDecoration(ContextCompat.getDrawable(context!!, R.drawable.decoration_divider_search)!!))

        transfer_email_input.textChangeListener(viewModel::emailInputChanged)
        transfer_email_proceed.setOnClickListener { viewModel.emailVerified(transfer_email_input.getText()) }

        transfer_confirm.setOnClickListener { viewModel.transferConfirmed() }
    }

    override fun showSection(section: String?) {
        transfer_section.text = section
    }

    override fun showContactsState(active: Boolean) {
        group_transfer_contacts.postView { it.isVisible = active }
    }

    override fun showEmailState(active: Boolean) {
        group_transfer_email.postView { it.isVisible = active }
    }

    override fun showConfirmState(active: Boolean) {
        group_transfer_confirm.postView { it.isVisible = active }
    }

    override fun showEmail(email: String?) {
        transfer_email_input.setText(email)
        transfer_confirm_headline.text = getString(R.string.ticket_details_drawer_confirm_headline, email)
    }

    override fun showContacts(items: List<Contact>) {
        contactsAdapter.submitList(items)
    }

    override fun showContactsLoading(active: Boolean) {
        contactsAdapter.isLoading = active
    }

    override fun showEmailValid(valid: Boolean) {
        transfer_email_input.isValid(valid)
    }

    override fun showProceedEnabled(enabled: Boolean) {
        transfer_email_proceed.state = if (enabled) State.ENABLED else State.DISABLED_FULL
    }

    override fun showTransferClosed(delay: Long) {
        navigator.navigateBack.go(false to delay)
    }

    override fun showManualInputKeyboard(active: Boolean) {
        when {
            active -> transfer_email_input.postView { it.focus() }
            else -> {
                transfer_email_input.clearFocus()
                hideKeyboard()
            }
        }
    }

    override fun showTransferConfirmEnabled(enabled: Boolean) {
        transfer_confirm.state = if (enabled) StatefulButton.State.ENABLED else StatefulButton.State.DISABLED
    }

    override fun showActionIndicator(active: Boolean) {
        transfer_confirm.state = if (active) StatefulButton.State.LOADING else StatefulButton.State.ENABLED
    }

    override fun showSuccessAction() {
        transfer_confirm.state = StatefulButton.State.SUCCESS
        showTransferClosed(1500L)
    }

    override fun showError(message: String?) {
        super.showError(message)
        transfer_confirm.state = StatefulButton.State.DISABLED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_CONTACTS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } )) {
                viewModel.useContacts()
            }
        }
    }

    fun onBackPressed() {
        viewModel.transferBack()
    }

    companion object {
        private const val ARG_TICKET_ID = "ARG_TICKET_ID"
        private const val ARG_TRANSFER_STATE = "ARG_TRANSFER_STATE"
        private const val RC_CONTACTS_PERMISSION = 763

        fun newInstance(ticketId: String, state: TransferState) = TransferFragment().apply {
            arguments = Bundle(2).apply {
                putString(ARG_TICKET_ID, ticketId)
                putString(ARG_TRANSFER_STATE, state.name)
            }
        }
    }
}