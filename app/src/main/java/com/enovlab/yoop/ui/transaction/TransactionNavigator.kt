package com.enovlab.yoop.ui.transaction

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.enovlab.yoop.ble.UserScanner
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * @author vishaan
 */
class TransactionNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Pair<Boolean, Long>>()
    internal val navigateToTransactionDetails = PublishSubject.create<Pair<String, String>>()
    internal val navigateToTransactionCount = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToTransactionEdit = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToShare = PublishSubject.create<Pair<Uri, String>>()
    internal val navigateToTransactionReview = PublishSubject.create<ReviewParams>()
    internal val navigateToWebUrl = PublishSubject.create<String>()
    internal val navigateToLogin = PublishSubject.create<Unit>()
    internal val navigateToSignup = PublishSubject.create<Unit>()
    internal val navigateToConfirmation = PublishSubject.create<ConfirmationParams>()
    internal val navigateToInbox = PublishSubject.create<Unit>()
    internal val navigateToPayments = PublishSubject.create<Unit>()
    internal val navigateToMyTicketsRequested = PublishSubject.create<Unit>()
    internal val navigateToMyTicketsSecured = PublishSubject.create<Unit>()
    internal val navigateToTicketDetails = PublishSubject.create<TicketDetailsParams>()
    internal val navigateToTicketTransferContacts = PublishSubject.create<String>()
    internal val navigateToTicketTransferManual = PublishSubject.create<String>()
    internal val navigateToProfileCapture = PublishSubject.create<Unit>()
    internal val navigateToProfileCaptureIntro = PublishSubject.create<Unit>()
    internal val navigateToDiscover = PublishSubject.create<Unit>()
    internal val navigateToBluetoothSettings = PublishSubject.create<Unit>()
    internal val navigateToRequestPermissions = PublishSubject.create<Unit>()
    internal val userScannerState = BehaviorSubject.create<UserScanner.State>()

    internal var editMode = false
    internal var offerGroupId: String? = null

    data class ReviewParams(val id: String,
                            val type: String? = null, val offerGroupId: String? = null, val ticketId: String? = null,
                            val count: Int? = null, val amount: Int? = null, val chanceToken: String? = null,
                            val isUpdate: Boolean = false, val isOverview: Boolean = false,
                            val isFixPayment: Boolean = false, val isClaimTickets: Boolean = false,
                            val delay: Long = 0L)

    data class ConfirmationParams(val id: String, val type: String, val hasPaid: Boolean, val delay: Long = 0L)

    data class TicketDetailsParams(val id: String, val ticketId: String? = null, val delay: Long = 0L)

    enum class Navigation {
        DETAILS, COUNT, EDIT, REVIEW_FIX_PAYMENT, REVIEW_CLAIM_TICKETS, TICKET_DETAILS, ACCEPT_TOKEN
    }
}