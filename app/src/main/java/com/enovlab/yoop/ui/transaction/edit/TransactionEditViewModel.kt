package com.enovlab.yoop.ui.transaction.edit

import com.enovlab.yoop.api.response.ChancesResponse
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.Demand
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.event.MarketplaceInfo
import com.enovlab.yoop.data.entity.event.OfferGroup
import com.enovlab.yoop.data.entity.event.Performer
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.MarketplaceRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem
import com.enovlab.yoop.utils.CustomTimer
import com.enovlab.yoop.utils.Flowables
import com.enovlab.yoop.utils.ext.isLessThan24HoursLeft
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TransactionEditViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val marketplaceRepository: MarketplaceRepository) : StateViewModel<TransactionEditView>() {

    internal lateinit var id: String
    internal lateinit var type: MarketplaceType
    internal lateinit var offerGroupId: String

    private var currency: String? = null
    private var reservePrice: Int = 0
    private var currentAmount: Int = 0
    private var currentTicketCount: Int = 0
    private var currentChance: Chance? = null
    private var currentDemand: Demand? = null

    private var ticketCount: Int = 0
    private var inputAmount = 0
    private var inputChance: Chance? = null
    private var chancesMap: List<ChanceMap>? = null
    private var tokenChances: String? = null
    private var isTransactionClosed = false

//    private var singleTimeData = AtomicBoolean(false)
    private var timer: CustomTimer? = null

    override fun start() {
        observeEvent()
        load { eventsRepository.loadEvent(id).toCompletable() }
    }

    override fun stop() {
        super.stop()
        timer?.cancel()
    }

    private fun observeEvent() {
        val source = when (type) {
            MarketplaceType.AUCTION -> Flowables.combineLatest(
                eventsRepository.observeEvent(id),
                marketplaceRepository.chances(offerGroupId, if (ticketCount > 0) ticketCount else currentTicketCount).toFlowable())
                .map {
                    chancesMap = mapChances(it.second.chances)
                    tokenChances = it.second.token
                    it.first
                }
            else -> eventsRepository.observeEvent(id)
        }

        disposables += source.subscribe({ event ->

            performerPictureUrl(event.performers)?.let { url ->
                view?.showSummaryPerformerPicture(url)
            }

            if (event.marketplaceInfo != null) {
                val marketplace = event.marketplaceInfo?.find { it.type == type }!!
                val offerGroup = marketplace.offerGroups?.find { it.id == offerGroupId }!!
                val offer = offerGroup.offer!!

                currency = currencySign(event.currency!!)
                reservePrice = offerGroup.reservePrice?.toInt() ?: 0
                currentAmount = offer.amount?.toInt() ?: 0
                currentTicketCount = offer.numberOfTokens ?: 0
                currentChance = offer.chance
                currentDemand = offerGroup.demand

                setupTransactionEnding(marketplace.endDate)

                view?.showDescription(offerGroup.description)
                setupSummary(marketplace, offerGroup)

                val count = when {
                    ticketCount > 0 -> ticketCount
                    else -> currentTicketCount
                }
                view?.showPickerCount(createCountItems(marketplace.userTicketLimitRemaining ?: 0 + currentTicketCount, count))

                view?.showMoreEventName(event.shortName)

                when (marketplace.type) {
                    MarketplaceType.DRAW -> {
                        view?.showMoreListMarketplace()

                        val userMadeChanges = ticketCount > 0 && ticketCount != currentTicketCount
                        view?.showHeadlineChanged(userMadeChanges)
                        view?.showMyRequestHeadline(userMadeChanges)
                        view?.showPicker(userMadeChanges)
                        view?.showInput(false)
                        view?.showSaveChanges(userMadeChanges && !isTransactionClosed)
                        view?.showTicketSelected(!userMadeChanges)
                    }
                    MarketplaceType.AUCTION -> {
                        view?.showMoreOnSaleMarketplace()

                        val userMadeChanges = (ticketCount > 0 && ticketCount != currentTicketCount)
                            || (inputAmount > 0 && inputAmount != currentAmount)
                        view?.showHeadlineChanged(userMadeChanges)
                        view?.showMyOfferHeadline(userMadeChanges)
                        view?.showInput(true)
                        view?.showInputAmountCurrency(currency)
                        view?.showInputAmount(if (inputAmount > 0) inputAmount.toString() else currentAmount.toString())
                        view?.showPicker(false)
                        view?.showTicketSelected(true)
                    }
                }

                view?.showTicketCountSelected(if (ticketCount > 0) ticketCount else currentTicketCount)
            }
        }, { error ->
            Timber.e(error)
        })
    }

    private fun setupSummary(marketplace: MarketplaceInfo, offerGroup: OfferGroup) {
        when {
            marketplace.type == MarketplaceType.AUCTION -> {

                setupTransactionEnding(marketplace.endDate)

                when (offerGroup.demand) {
                    Demand.HIGH -> {
                        view?.showDemandTitle(true)
                        view?.showHighDemand()
                    }
                    Demand.DEMAND_EXCEEDS_SUPPLY -> {
                        view?.showDemandTitle(true)
                        view?.showExceedsSupplyDemand()

                        view?.showSummaryMinAskExceedsSupplyDemand()
                        view?.showSummaryMinOffer(true)
                        view?.showSummaryMinOfferPrice(currency, offerGroup.minQualifyingPrice?.toInt() ?: 0)
                    }
                    else -> {
                        view?.showDemandTitle(false)
                    }
                }

                view?.showSummaryMinAskPriceTitle()
                view?.showSummaryOnSalePeopleIcon()
                view?.showSummaryAverageOfferPriceTitle()
                view?.showSummaryAverageOfferPrice(currency, offerGroup.averageOfferPrice?.toInt() ?: 0)

                view?.showInputAmountCurrency(currency)
            }
            marketplace.type == MarketplaceType.DRAW -> {
                view?.showSummaryListPriceTitle()
                view?.showSummaryListTicketsTitle()
                view?.showSummaryListTicketsIcon()
                view?.showSummaryListTicketsCount(offerGroup.numberOfTokens ?: 0)
            }
        }

        view?.showSummaryPrice(currency, reservePrice)
    }

    private fun setupTransactionEnding(endDate: Date?) {
        when {
            Date() <= endDate -> when {
                endDate?.isLessThan24HoursLeft() == true -> {
                    timer = CustomTimer(endDate)
                    timer?.tickListener = {
                        if (it.hours == 0 && it.minutes in 1..5) {
                            view?.showTransactionClosesLessThen5Mins(true)
                            view?.showTransactionClosesMinutes(it.minutes)
                        } else if (it.hours == 0 && it.minutes == 0 && it.seconds > 0) {
                            view?.showTransactionClosesLessThen5Mins(true)
                            view?.showTransactionClosesSeconds(it.seconds)
                        }
                    }
                    timer?.finishListener = {
                        isTransactionClosed = true
                        view?.showTransactionClosed()
                        view?.showSaveChanges(false)
                    }
                    timer?.start()
                }
                else -> view?.showTransactionClosesLessThen5Mins(false)
            }
            else -> {
                isTransactionClosed = true
                view?.showTransactionClosesLessThen5Mins(true)
                view?.showTransactionClosed()
                view?.showSaveChanges(false)
            }
        }
    }

    internal fun countSelected(item: CountItem, total: Int) {
        ticketCount = item.count
        view?.showPickerCount(createCountItems(total, ticketCount))
        view?.showTicketCountSelected(ticketCount)

        when {
            type == MarketplaceType.DRAW -> {
                view?.showSaveChanges(ticketCount > 0 && ticketCount != currentTicketCount && !isTransactionClosed)
            }
            type == MarketplaceType.AUCTION -> {
                view?.showSaveChanges((ticketCount > 0 && ticketCount != currentTicketCount) && !isTransactionClosed
                    || inputAmount != currentAmount)

                disposables += marketplaceRepository.chances(offerGroupId, ticketCount).subscribe({
                    chancesMap = mapChances(it.chances)
                    tokenChances = it.token

                    val chances = it.chances
                    val minOffer = when {
                        chances.poor?.toInt() ?: 0 > 0 -> chances.poor!!.toInt()
                        chances.low?.toInt() ?: 0 > 0 -> chances.low!!.toInt()
                        chances.good?.toInt() ?: 0 > 0 -> chances.good!!.toInt()
                        chances.good?.toInt() ?: 0 > 0 -> chances.great!!.toInt()
                        else -> 0
                    }

                    view?.showSummaryMinOfferPrice(currency, minOffer)

                    amountChanged(if (inputAmount > 0) inputAmount.toString() else currentAmount.toString())
                }, { error ->
                    Timber.e(error, "Error retrieving chances.")
                })
            }
        }
    }

    internal fun amountChanged(amountInput: String) {
        if (amountInput.isNotBlank()) {
            inputAmount = amountInput.toInt()

            if (inputAmount == 0) {
                view?.showChancesDefault()
                view?.showSaveChanges(false)
                return
            }

            if (inputAmount == currentAmount) {
                when (currentChance) {
                    Chance.NEGLIGIBLE -> view?.showChancesNegligible()
                    else -> view?.showChances(currentChance!!)
                }
                view?.showSaveChanges(ticketCount > 0 && ticketCount != currentTicketCount && !isTransactionClosed)
                return
            }

            if (inputAmount < reservePrice) {
                view?.showChancesWont(reservePrice)
                view?.showSaveChanges(false)
            } else {
                var chanceMap: ChanceMap? = null

                for (cm in chancesMap!!) {
                    if (inputAmount >= cm.amount) {
                        chanceMap = cm
                        break
                    }
                }
                inputChance = chanceMap?.chance

                when {
                    chanceMap != null -> view?.showChances(chanceMap.chance)
                    else -> view?.showChancesNegligible()
                }

                view?.showSaveChanges(!isTransactionClosed)
            }
        } else {
            inputAmount = 0
            view?.showChancesDefault()
            view?.showSaveChanges(false)
        }
    }

    internal fun ticketsSelectedClicked() {
        when (type) {
            MarketplaceType.DRAW -> {
                view?.showHeadlineChanged(true)
                view?.showMyRequestHeadline(true)
                view?.showPicker(true)
                view?.showTicketSelected(false)
            }
            MarketplaceType.AUCTION -> view?.showTicketSelectedDrawer()
        }
    }

    internal fun deleteClicked() {
        view?.showDeleteConfirmation()
    }

    internal fun deleteConfirmClicked() {
        action {
            when (type) {
                MarketplaceType.DRAW -> marketplaceRepository.deleteListRequest(offerGroupId)
                else -> marketplaceRepository.deleteOnSaleOffer(offerGroupId)
            }.doOnComplete {
                view?.showEditingFinished(true)
            }
        }
    }

    internal fun closeClicked() {
        val hasChanges = when (type) {
            MarketplaceType.DRAW -> ticketCount > 0 && ticketCount != currentTicketCount
            else -> (inputAmount > 0 && inputAmount != currentAmount) || (ticketCount > 0 && ticketCount != currentTicketCount)
        }
        when {
            hasChanges -> view?.showDiscardChangesConfirmation()
            else -> view?.showEditingFinished(false)
        }
    }

    internal fun moreClicked() {
        view?.showMoreDialog()
    }

    internal fun seeOrderSummaryClicked() {
        view?.showReview(id, type.name, offerGroupId, currentTicketCount, currentAmount, tokenChances, isOverview = true)
    }

    internal fun saveChangesClicked() {
        view?.showReview(id, type.name, offerGroupId,
            if (ticketCount > 0) ticketCount else currentTicketCount,
            if (inputAmount > 0) inputAmount else currentAmount,
            tokenChances, isUpdate = true)
    }

    private fun performerPictureUrl(performers: List<Performer>?): String? {
        if (performers == null || performers.isEmpty()) return null
        return performers.first().defaultMedia?.url
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }

    private fun createCountItems(ticketAvailable: Int, selected: Int = 0): List<CountItem> {
        val items = mutableListOf<CountItem>()
        for (i in 1..ticketAvailable)
            items.add(CountItem(i, i == selected))
        return items
    }

    private fun mapChances(chances: ChancesResponse.Chances): List<ChanceMap> {
        val list = mutableListOf<ChanceMap>()

        if (chances.great != null) {
            list.add(ChanceMap(Chance.GREAT, chances.great))
        }
        if (chances.good != null) {
            list.add(ChanceMap(Chance.GOOD, chances.good))
        }
        if (chances.low != null) {
            list.add(ChanceMap(Chance.LOW, chances.low))
        }
        if (chances.poor != null) {
            list.add(ChanceMap(Chance.POOR, chances.poor))
        }

        return list
    }

    private data class ChanceMap(val chance: Chance, val amount: Double)
}