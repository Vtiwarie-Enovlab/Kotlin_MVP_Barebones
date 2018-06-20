package com.enovlab.yoop.ui.transaction.ticket.details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.PagerSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketAdapter
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItemDecoration
import com.enovlab.yoop.ui.widget.HorizontalSmoothScrollLayoutManager
import com.enovlab.yoop.utils.ext.requiresContactsPermission
import com.enovlab.yoop.utils.ext.scrollListener
import kotlinx.android.synthetic.main.fragment_ticket_details.*
import kotlinx.android.synthetic.main.layout_ticket_details_app_bar.*
import kotlinx.android.synthetic.main.layout_ticket_details_more_drawer.*
import kotlinx.android.synthetic.main.layout_ticket_details_transfer_drawer.*


class TicketDetailsFragment : TransactionFragment<TicketDetailsView, TicketDetailsViewModel>(), TicketDetailsView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = TicketDetailsViewModel::class.java

    private val adapter by lazy { TicketAdapter() }
    private val transferDialog by lazy { BottomSheetDialog(context!!) }
    private val moreDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.eventId = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.ticketId = arguments?.getString(ARG_TICKET_ID)

        viewModel.scannerState = navigator.userScannerState
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ticket_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        more.setOnClickListener { viewModel.moreClicked(adapter.getItem(viewModel.page), adapter.itemCount) }

        adapter.listenerTransfer = { viewModel.transferTicket(it.id) }
        adapter.listenerCreateId = { viewModel.createIdClicked() }
        adapter.listenerChanges = { viewModel.ticketsChanged() }
        ticket_list.adapter = adapter

        val layoutManager = HorizontalSmoothScrollLayoutManager(context!!)
        ticket_list.layoutManager = layoutManager

        ticket_list.scrollListener {
            viewModel.page = layoutManager.findFirstCompletelyVisibleItemPosition()
        }
        PagerSnapHelper().attachToRecyclerView(ticket_list)
        ticket_list.addItemDecoration(TicketItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_medium)))

        transferDialog.setContentView(R.layout.layout_ticket_details_transfer_drawer)
        transferDialog.transfer_close.setOnClickListener { transferDialog.dismiss() }
        transferDialog.transfer_contacts.setOnClickListener {
            when {
                context!!.requiresContactsPermission() -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), RC_CONTACTS_PERMISSION)
                }
                else -> viewModel.transferUseContacts()
            }
        }
        transferDialog.transfer_manual.setOnClickListener { viewModel.transferInputManually() }

        moreDialog.setContentView(R.layout.layout_ticket_details_more_drawer)
        moreDialog.ticket_details_more_cancel.setOnClickListener { moreDialog.dismiss() }
        moreDialog.ticket_details_more_transfer.setOnClickListener { viewModel.transferTicket() }
        moreDialog.ticket_details_more_reclaim.setOnClickListener { viewModel.cancelTransfer() }
        moreDialog.ticket_details_more_return.setOnClickListener { viewModel.cancelTransfer() }
        moreDialog.ticket_details_more_resend.setOnClickListener { viewModel.resendTicket() }
        moreDialog.ticket_details_more_receipt.setOnClickListener { viewModel.seeReceipt() }
        moreDialog.ticket_details_more_reminder.setOnClickListener { viewModel.sendReminder() }
        moreDialog.ticket_details_more_assign_to_user.setOnClickListener { viewModel.assignToUser() }

        ticket_find_more.setOnClickListener { navigator.navigateToDiscover.go() }
    }

    override fun showTicketItems(items: List<TicketItem>) {
        adapter.submitList(items)
    }

    override fun showNoTickets(active: Boolean) {
        ticket_group_empty.isVisible = active
    }

    override fun showPageIndicatorsActive(active: Boolean) {
        ticket_list_indicator.isVisible = active
    }

    override fun showPageIndicators(pages: Int, currentPage: Int) {
        for (page in 0 until pages) {
            ticket_list_indicator.addTab(ticket_list_indicator.newTab(), page == currentPage)
            (ticket_list_indicator.getChildAt(0) as ViewGroup).forEach {
                it.setOnTouchListener { _, _ -> true }
            }
        }
    }

    override fun showPageSelectedIndicator(page: Int) {
        ticket_list_indicator.getTabAt(page)?.select()
    }

    override fun showPage(page: Int) {
        ticket_list.smoothScrollToPosition(page)
    }

    override fun showEventName(name: String?) {
        title.text = name
        moreDialog.ticket_details_more_title.text = name
    }

    override fun showEventDateLocation(date: String?, location: String?) {
        event_location_date.text = "$date • $location"
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    override fun showTransferFlowDialog(active: Boolean) {
        when {
            active -> transferDialog.show()
            else -> transferDialog.dismiss()
        }
    }

    override fun showTransferFlowContacts(ticketId: String) {
        navigator.navigateToTicketTransferContacts.go(ticketId)
    }

    override fun showTransferFlowManualInput(ticketId: String) {
        navigator.navigateToTicketTransferManual.go(ticketId)
    }

    override fun showProfileCapture() {
        navigator.navigateToProfileCapture.go()
    }

    override fun showProfileIntro() {
        navigator.navigateToProfileCaptureIntro.go()
    }

    override fun showMoreDialogSubtitle(page: Int, total: Int) {
        moreDialog.ticket_details_more_subtitle.text = getString(R.string.ticket_details_more_subtitle, page, total)
    }

    override fun showMoreDialog(active: Boolean) {
        when {
            active -> moreDialog.show()
            else -> moreDialog.dismiss()
        }
    }

    override fun showMoreOwnerDialog() {
        moreDialog.ticket_details_more_transfer.isVisible = true
        moreDialog.ticket_details_more_receipt.isVisible = true
        moreDialog.ticket_details_more_reclaim.isVisible = false
        moreDialog.ticket_details_more_resend.isVisible = false
        moreDialog.ticket_details_more_reminder.isVisible = false
        moreDialog.ticket_details_more_return.isVisible = false
        moreDialog.ticket_details_more_assign_to_user.isVisible = false
    }

    override fun showMoreUnassignedDialog() {
        moreDialog.ticket_details_more_transfer.isVisible = true
        moreDialog.ticket_details_more_receipt.isVisible = true
        moreDialog.ticket_details_more_assign_to_user.isVisible = true
        moreDialog.ticket_details_more_reclaim.isVisible = false
        moreDialog.ticket_details_more_resend.isVisible = false
        moreDialog.ticket_details_more_reminder.isVisible = false
        moreDialog.ticket_details_more_return.isVisible = false
    }

    override fun showMoreAssigneeDialog() {
        moreDialog.ticket_details_more_return.isVisible = true
        moreDialog.ticket_details_more_transfer.isVisible = false
        moreDialog.ticket_details_more_receipt.isVisible = false
        moreDialog.ticket_details_more_reclaim.isVisible = false
        moreDialog.ticket_details_more_resend.isVisible = false
        moreDialog.ticket_details_more_reminder.isVisible = false
        moreDialog.ticket_details_more_assign_to_user.isVisible = false
    }

    override fun showMorePendingDialog() {
        moreDialog.ticket_details_more_resend.isVisible = true
        moreDialog.ticket_details_more_reclaim.isVisible = true
        moreDialog.ticket_details_more_receipt.isVisible = true
        moreDialog.ticket_details_more_return.isVisible = false
        moreDialog.ticket_details_more_transfer.isVisible = false
        moreDialog.ticket_details_more_reminder.isVisible = false
        moreDialog.ticket_details_more_assign_to_user.isVisible = false
    }

    override fun showMoreAssignedDialog() {
        moreDialog.ticket_details_more_reminder.isVisible = true
        moreDialog.ticket_details_more_reclaim.isVisible = true
        moreDialog.ticket_details_more_receipt.isVisible = true
        moreDialog.ticket_details_more_resend.isVisible = false
        moreDialog.ticket_details_more_return.isVisible = false
        moreDialog.ticket_details_more_transfer.isVisible = false
        moreDialog.ticket_details_more_assign_to_user.isVisible = false
    }

    override fun showReceipt(eventId: String, ticketId: String) {
        navigator.navigateToTransactionReview.go(TransactionNavigator.ReviewParams(id = eventId, ticketId = ticketId))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_CONTACTS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                viewModel.transferUseContacts()
            }
        }
    }

    override fun showBluetoothSettings() {
        navigator.navigateToBluetoothSettings.go()
    }

    override fun showRequestPermissions() {
        navigator.navigateToRequestPermissions.go()
    }

    companion object {
        private const val ARG_EVENT_ID = "ARG_EVENT_ID"
        private const val ARG_TICKET_ID = "ARG_TICKET_ID"
        private const val RC_CONTACTS_PERMISSION = 763

        fun newInstance(eventId: String, ticketId: String? = null) = TicketDetailsFragment().apply {
            arguments = Bundle(2).apply {
                putString(ARG_EVENT_ID, eventId)
                putString(ARG_TICKET_ID, ticketId)
            }
        }
    }
}