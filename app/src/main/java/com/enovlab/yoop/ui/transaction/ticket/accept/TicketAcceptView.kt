package com.enovlab.yoop.ui.transaction.ticket.accept

import com.enovlab.yoop.ui.base.state.StateView

interface TicketAcceptView : StateView {
    fun showEventName(name: String?)
    fun showEventDateLocation(date: String?, location: String?)
    fun showPerformerPicture(url: String?)

    fun showClaimTicketHeadline()
    fun showTicketClaimedHeadline()
    fun showEmailVerification(userEmail: String)
    fun showLegalLinks(termsUrl: String, privacyUrl: String)

    fun showStateTokenInvalid(active: Boolean)
    fun showStateNotAuthorized(active: Boolean)
    fun showStateEmailVerification(active: Boolean)
    fun showStateTokensAssigned(active: Boolean)

    fun showTicketClaimed(eventId: String, delay: Long = 0L)
    fun showCloseAssignmentDialog()
    fun showClosedAssignment()
}