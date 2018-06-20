package com.enovlab.yoop.ui.transaction.count

import android.net.Uri
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem

interface TransactionCountView : StateView {
    fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String)

    fun showDescription(description: String?)
    fun showDemandTitle(active: Boolean)
    fun showHighDemand()
    fun showExceedsSupplyDemand()

    fun showPickerCount(items: List<CountItem>)
    fun showReview(id: String, type: String, offerGroupId: String, count: Int,
                   amount: Int? = null, chanceToken: String? = null, delay: Long = 0L)

    fun showSummaryPerformerPicture(url: String)
    fun showSummaryListPriceTitle()
    fun showSummaryPrice(currency: String?, price: Int)
    fun showSummaryListTicketsTitle()
    fun showSummaryListTicketsIcon()
    fun showSummaryListTicketsCount(count: Int)
    fun showSummaryMinAskPriceTitle()
    fun showSummaryOnSalePeopleIcon()
    fun showSummaryAverageOfferPriceTitle()
    fun showSummaryAverageOfferPrice(currency: String?, price: Int)
    fun showSummaryMinAskExceedsSupplyDemand()
    fun showSummaryMinOffer(active: Boolean)
    fun showSummaryMinOfferPrice(currency: String?, price: Int)

    fun showPicker(active: Boolean, duration: Long, animationListener: () -> Unit)
    fun showInput(active: Boolean, showKeyboard: Boolean, duration: Long)
    fun showTicketSelected(active: Boolean)
    fun showTicketCountSelected(count: Int)
    fun showProceedToReviewEnabled(enabled: Boolean)
    fun showProceedToReview(active: Boolean)
    fun showInputAmountCurrency(currency: String?)
    fun showInputAmountHint(amountHint: String)
    fun showInputAmount(amount: String)
    fun showChancesDefault()
    fun showChances(chance: Chance)
    fun showChancesWont(minAskPrice: Int)
    fun showChancesNegligible()
    fun showTicketSelectedDrawer(items: List<CountItem>)
    fun showTransactionClosesLessThen5Mins(active: Boolean)
    fun showTransactionClosesMinutes(minutes: Int)
    fun showTransactionClosesSeconds(seconds: Int)
    fun showTransactionClosed()
    fun showUserLimitActive(active: Boolean)
    fun showUserLimit(requested: Int, total: Int)
}