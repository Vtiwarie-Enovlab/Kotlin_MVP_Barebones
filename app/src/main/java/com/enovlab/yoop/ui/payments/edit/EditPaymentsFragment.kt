package com.enovlab.yoop.ui.payments.edit

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.ui.base.StackableFragment
import com.enovlab.yoop.ui.payments.PaymentsFragment
import com.enovlab.yoop.ui.payments.add.billing.adapter.CountriesAdapter
import kotlinx.android.synthetic.main.fragment_edit_payments.*
import kotlinx.android.synthetic.main.layout_countries_list.*
import kotlinx.android.synthetic.main.layout_countries_list.view.*
import kotlinx.android.synthetic.main.layout_edit_payments_app_bar.*
import kotlinx.android.synthetic.main.layout_payment_edit_confirm.*
import kotlinx.android.synthetic.main.layout_payments_billing_template.*

/**
 * @author vishaan
 */
class EditPaymentsFragment : PaymentsFragment<EditPaymentsView, EditPaymentsViewModel>(), EditPaymentsView, StackableFragment {
    override val vmClass = EditPaymentsViewModel::class.java

    private val confirmDialog by lazy { BottomSheetDialog(context!!) }
    private val countriesDialog by lazy { BottomSheetDialog(context!!) }
    private val countriesAdapter by lazy { CountriesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.paymentMethodId = it.getString(ARG_PAYMENT_METHOD_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(false to 0L) }
        delete_payment.setOnClickListener { showDeleteConfirmation(true) }
        edit_payments_save.setOnClickListener {
            viewModel.updatePayment()
        }

        input_card_holder.isEnabled = false
        input_card_holder.getEditText().setTextColor(ContextCompat.getColor(context!!, R.color.color_white_alpha_50))

        //zip code field
        showZipCodeFocus(true)
        input_zip.textChangeListener { viewModel.zipCodeChanged(input_zip.getText()) }
        switch_default.setOnCheckedChangeListener { v, checked ->
            viewModel.cardSetDefaultChanged(switch_default?.isChecked ?: false)
        }

        //confirmation dialog
        confirmDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_payment_edit_confirm, null))
        confirmDialog.delete_confirm.setOnClickListener {
            viewModel.deleteConfirmClicked()
            confirmDialog.container_delete_confirm.isInvisible = true
        }
        confirmDialog.delete_cancel.setOnClickListener {
            showDeleteConfirmation(false)
        }
        confirmDialog.delete_failed_got_it.setOnClickListener {
            showDeleteConfirmation(false)
        }

        //countries dialog
        val countriesSheet = LayoutInflater.from(context!!)
            .inflate(R.layout.layout_countries_list, null)
        countriesDialog.setContentView(countriesSheet)

        input_country.setOnClickListener {
            showCountriesDialog(true)
        }
        countriesSheet.countries_list.adapter = countriesAdapter
        (countriesSheet.countries_list.adapter as CountriesAdapter).listener = { viewModel.countrySelected(it) }
        countriesAdapter.submitList(viewModel.countries)
    }

    override fun showError(message: String?) {
        confirmDialog.container_delete_confirm.isVisible = false
        confirmDialog.container_delete_failed.isVisible = true
    }

    override fun showErrorNoConnection() {
        confirmDialog.hide()
        super.showErrorNoConnection()
    }

    override fun showDeleteConfirmation(show: Boolean) {
        if (show) {
            confirmDialog.container_delete_confirm.isVisible = true
            confirmDialog.container_delete_failed.isVisible = false
            confirmDialog.show()
        } else {
            confirmDialog.hide()
        }
    }

    override fun showActionIndicator(active: Boolean) {
        if (confirmDialog.isShowing) confirmDialog.delete_progress.isVisible = active
    }

    override fun showFailedDeleteMessage() {
        //TODO show "hold up" message
    }

    override fun showCard(cardType: CardType?) {
        when (cardType) {
            CardType.MC -> card_logo.setImageResource(R.drawable.ic_mastercard_large)
            else -> card_logo.setImageResource(R.drawable.ic_visa_large)
        }
    }

    override fun showLastFour(lastFour: String?) {
        card_number.text = lastFour
    }

    override fun showExpiryDate(expiryDate: String?) {
        expiry.text = expiryDate
    }

    override fun showDefaultButtonEnabled(enabled: Boolean) {
        switch_default.isEnabled = enabled
    }

    override fun showDefaultButtonChecked(On: Boolean) {
        switch_default.isChecked = On
    }

    override fun showMakeDefaultLabel(show: Boolean) {
        switch_default_label?.isVisible = show
    }

    override fun showMakeDefault(show: Boolean) {
        switch_default?.isVisible = show
    }

    override fun showSaveButton(show: Boolean) {
        edit_payments_save.isVisible = show
    }

    override fun showCardHolderNameValid(valid: Boolean) {
        input_card_holder.isValid(valid)
    }

    override fun showZipCodeValid(valid: Boolean) {
        input_zip.isValid(valid)
    }

    override fun showCountriesDialog(show: Boolean) {
        countriesDialog.countries_list.isVisible = show
        if (show) countriesDialog.show() else countriesDialog.hide()
    }

    override fun showCountryText(text: String) {
        input_country.setText(text)
    }

    override fun showCardHolderName(text: String) {
        input_card_holder.setText(text)
    }

    override fun showCountry(text: String) {
        input_country.setText(text)
    }

    override fun showZipCode(text: String) {
        input_zip.setText(text)
    }

    override fun showCardHolderNameFocus(focus: Boolean) {
        input_card_holder.focus()
    }

    override fun showZipCodeFocus(focus: Boolean) {
        input_zip.focus()
    }

    override fun showSuccessAction() {
        navigator.navigateBack.go(false to 0L)
    }

    override fun onBackPressed() {
        childFragmentManager.popBackStack()
    }

    override fun showCodeLabelZip() {
        input_zip.setHint(R.string.payments_zip_postal)
    }

    override fun showCodeLabelPostal() {
        input_zip.setHint(R.string.payments_billing_postal)
    }

    companion object {
        internal const val ARG_PAYMENT_METHOD_ID = "ARG_PAYMENT_METHOD_ID"
        fun newInstance(paymentMethodId: String) = EditPaymentsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PAYMENT_METHOD_ID, paymentMethodId)
            }
        }
    }
}