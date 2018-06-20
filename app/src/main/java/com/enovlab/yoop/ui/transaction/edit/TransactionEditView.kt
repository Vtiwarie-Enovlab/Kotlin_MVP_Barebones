package com.enovlab.yoop.ui.transaction.edit

import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem

interface TransactionEditView : StateView {
    fun showDescription(description: String?)
    fun showDemandTitle(active: Boolean)
    fun showHighDemand()
    fun showExceedsSupplyDemand()

    fun showTransactionClosesLessThen5Mins(active: Boolean)
    fun showTransactionClosesMinutes(minutes: Int)
    fun showTransactionClosesSeconds(seconds: Int)
    fun showTransactionClosed()

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

    fun showPickerCount(items: List<CountItem>)
    fun showTicketSelected(active: Boolean)
    fun showTicketCountSelected(count: Int)

    fun showHeadlineChanged(changed: Boolean)

    fun showMyRequestHeadline(active: Boolean)
    fun showPicker(active: Boolean)

    fun showMyOfferHeadline(active: Boolean)
    fun showInput(active: Boolean)
    fun showInputAmountCurrency(currency: String?)
    fun showInputAmount(amount: String)
    fun showChancesDefault()
    fun showChances(chance: Chance)
    fun showChancesWont(minAskPrice: Int)
    fun showChancesNegligible()
    fun showTicketSelectedDrawer()

    fun showSaveChanges(active: Boolean)

    fun showDeleteConfirmation()
    fun showEditingFinished(finish: Boolean)
    fun showDiscardChangesConfirmation()
    fun showMoreDialog()
    fun showMoreOnSaleMarketplace()
    fun showMoreListMarketplace()
    fun showMoreEventName(name: String?)

    fun showReview(id: String, type: String, offerGroupId: String, count: Int,
                   amount: Int? = null, chanceToken: String? = null,
                   delay: Long = 0L,
                   isUpdate: Boolean = false, isOverview: Boolean = false)
}