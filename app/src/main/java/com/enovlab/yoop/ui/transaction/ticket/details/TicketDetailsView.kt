package com.enovlab.yoop.ui.transaction.ticket.details

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.TicketItem

interface TicketDetailsView : StateView {
    fun showTicketItems(items: List<TicketItem>)
    fun showNoTickets(active: Boolean)
    fun showPageIndicatorsActive(active: Boolean)
    fun showPageIndicators(pages: Int, currentPage: Int)
    fun showPageSelectedIndicator(page: Int)
    fun showPage(page: Int)
    fun showEventName(name: String?)
    fun showEventDateLocation(date: String?, location: String?)
    fun showTransferFlowDialog(active: Boolean)
    fun showTransferFlowContacts(ticketId: String)
    fun showTransferFlowManualInput(ticketId: String)
    fun showProfileCapture()
    fun showProfileIntro()
    fun showMoreOwnerDialog()
    fun showMoreUnassignedDialog()
    fun showMoreAssigneeDialog()
    fun showMorePendingDialog()
    fun showMoreAssignedDialog()
    fun showMoreDialogSubtitle(page: Int, total: Int)
    fun showMoreDialog(active: Boolean)
    fun showReceipt(eventId: String, ticketId: String)
    fun showBluetoothSettings()
    fun showRequestPermissions()
}