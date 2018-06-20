package com.enovlab.yoop.ui.transaction.review

import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.ui.base.state.StateView

interface TransactionReviewView : StateView {
    fun showEventName(name: String?)
    fun showEventDateLocation(date: String?, location: String?)
    fun showDescription(count: Int, description: String?)

    fun showHasOffer(active: Boolean)
    fun showUserHasOffer()
    fun showUserHasRequest()

    fun showReviewHeadline(active: Boolean)
    fun showPriceDetails(active: Boolean)
    fun showPaymentDetails(active: Boolean)
    fun showCheckout(active: Boolean)
    fun showAddPayment(active: Boolean)
    fun showNotVerified(active: Boolean)
    fun showNonAuthorized(active: Boolean)

    fun showListHeadline()
    fun showOnSaleHeadline()

    fun showTicketPrice(currency: String, price: Double, count: Int)
    fun showSubtotalPrice(currency: String, price: Double)
    fun showTotalPrice(currency: String, price: Double)
    fun showTicketFee(currency: String, fee: Double, count: Int)

    fun showChoseListDate(date: String?)
    fun showChoseOnSaleDate(date: String?)

    fun showLegalLinks(termsUrl: String, privacyUrl: String)
    fun showVerificationEmail(email: String)

    fun showPaymentCard(active: Boolean)
    fun showAutoProcess(active: Boolean)
    fun showCardLastNumbers(lastNumbers: String)
    fun showCardTypeVisa()
    fun showCardTypeMasterCard()
    fun showCardPayAttemptFailed(active: Boolean)
    fun showAutoProcessEnabled(enabled: Boolean)
    fun showAutoProcessActive(active: Boolean)
    fun showPayments(payments: List<PaymentMethod>)

    fun showCheckoutList(update: Boolean)
    fun showCheckoutOnSale(update: Boolean)

    fun showLimitExceedsOfferError(requested: Int, total: Int)
    fun showLimitExceedsRequestError(requested: Int, total: Int)
    fun showTicketLimitExceeds()

    fun showCautionDialog()

    fun showConfirmation(id: String, type: String, hasPaid: Boolean)
    fun showPaymentsDialog(active: Boolean)
    fun showPaymentSelectingProgress(active: Boolean)
    fun showPaymentSelectingError()

    fun setNavigationEdit()

    // claim, failed flow
    fun showClaimTickets(active: Boolean)
    fun showDeclineTickets(active: Boolean)
    fun showHeadlineSelected()
    fun showHeadlineFix()
    fun showClaimTicketsEnabled(enabled: Boolean)
    fun showErrorCardExpired()
    fun showDeclinedPayment()
    fun showPaymentFailed(retryAttempts: Int)
    fun showClaimProgress(active: Boolean)
    fun showClaimSuccess()

    fun showHeadlineReceipt()
    fun showPaymentEditable(editable: Boolean)
    fun showPaymentProcessedDate(date: String)
}