package com.enovlab.yoop.ui.transaction.count

import android.content.Context
import com.enovlab.yoop.api.response.ChancesResponse
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.Demand
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.MarketplaceInfo
import com.enovlab.yoop.data.entity.event.OfferGroup
import com.enovlab.yoop.data.entity.event.Performer
import com.enovlab.yoop.data.manager.FileManager
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.MarketplaceRepository
import com.enovlab.yoop.inject.GlideApp
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem
import com.enovlab.yoop.utils.CustomTimer
import com.enovlab.yoop.utils.ext.intoBitmap
import com.enovlab.yoop.utils.ext.isLessThan24HoursLeft
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class TransactionCountViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val marketplaceRepository: MarketplaceRepository,
                    private val fileManager: FileManager) : StateViewModel<TransactionCountView>() {

    internal lateinit var id: String
    internal lateinit var type: MarketplaceType
    internal lateinit var offerGroupId: String

    private var selectedTickets: Int = 0
    private var inputAmount = 0

    private var singleTimeData = AtomicBoolean(false)
    private var currency: String? = null
    private var reservePrice: Int = 0
    private var isTransactionClosed = false
    private var timer: CustomTimer? = null

    private var chancesMap: List<ChanceMap>? = null
    private var tokenChances: String? = null
    private lateinit var event: Event

    override fun start() {
        observeEvent()
        action { eventsRepository.loadEvent(id).toCompletable() }
    }

    override fun stop() {
        super.stop()
        timer?.cancel()
    }

    internal fun countSelected(item: CountItem, total: Int) {
        view?.showPickerCount(createCountItems(total, item.count))

        when {
            type == MarketplaceType.DRAW -> {
                view?.showReview(id, type.name, offerGroupId, item.count, delay = 500L)
            }
            type == MarketplaceType.AUCTION -> {
                disposables += marketplaceRepository.chances(offerGroupId, item.count)
                    .subscribe({
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
                        view?.showUserLimitActive(false)

                        val prevSelection = selectedTickets
                        view?.showPicker(false, PICKER_ANIM_DURATION) {
                            view?.showInput(true, prevSelection == 0, INPUT_ANIM_DURATION)

                            view?.showTicketSelected(true)
                            view?.showProceedToReview(true)

                            amountChanged(inputAmount.toString())
                        }

                        view?.showTicketCountSelected(item.count)
                        selectedTickets = item.count
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
                view?.showProceedToReviewEnabled(false)
            } else {
                if ((reservePrice > 0 && inputAmount < reservePrice) || (chancesMap == null || chancesMap!!.isEmpty())) {
                    view?.showChancesWont(reservePrice)
                    view?.showProceedToReviewEnabled(false)
                } else {
                    var currentChance: ChanceMap? = null

                    for (chanceMap in chancesMap!!) {
                        if (inputAmount >= chanceMap.amount) {
                            currentChance = chanceMap
                            break
                        }
                    }

                    when {
                        currentChance != null -> view?.showChances(currentChance.chance)
                        else -> view?.showChancesNegligible()
                    }

                    view?.showProceedToReviewEnabled(!isTransactionClosed)
                }
            }
        } else {
            view?.showChancesDefault()
            view?.showProceedToReviewEnabled(false)
        }
    }

    internal fun ticketsSelectedClicked(total: Int) {
        view?.showTicketSelectedDrawer(createCountItems(total, selectedTickets))
    }

    internal fun proceed() {
        view?.showReview(id, type.name, offerGroupId, selectedTickets, inputAmount, tokenChances!!)
    }

    private fun observeEvent() {
        disposables += eventsRepository.observeEvent(id).subscribe({ event ->
            this.event = event

            performerPictureUrl(event.performers)?.let { url ->
                view?.showSummaryPerformerPicture(url)
            }

            if (event.marketplaceInfo != null) {
                val marketplace = event.marketplaceInfo?.find { it.type == type }!!
                val offerGroup = marketplace.offerGroups?.find { it.id == offerGroupId }!!

                currency = currencySign(event.currency!!)
                reservePrice = offerGroup.reservePrice!!.toInt()

                view?.showDescription(offerGroup.description)

                setupSummary(marketplace, offerGroup)

                setupInput(marketplace, offerGroup)
            }
        }, { error ->
            Timber.e(error)
        })
    }

    private fun setupSummary(marketplace: MarketplaceInfo, offerGroup: OfferGroup) {
        when {
            marketplace.type == MarketplaceType.AUCTION -> {

//                        marketplace.endDate = Date(1524772620000L)
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
                        view?.showSummaryMinOfferPrice(currency, offerGroup.minQualifyingPrice?.toInt() ?: 0) //TODO change later
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
                view?.showSummaryListTicketsCount(offerGroup.numberOfTokens!!)
            }
        }

        view?.showSummaryPrice(currency, reservePrice)
    }

    private fun setupInput(marketplace: MarketplaceInfo, offerGroup: OfferGroup) {
        if (!singleTimeData.getAndSet(true)) {
            val totalTickets = Math.min(offerGroup.numberOfTokens!!, marketplace.limitCount!!)
            val hasLimits = marketplace.userTicketLimitRemaining != null && marketplace.userTicketLimitRemaining!! > 0
                && marketplace.userTicketLimitRemaining != totalTickets
            when {
                hasLimits -> {
                    view?.showPickerCount(createCountItems(marketplace.userTicketLimitRemaining!!))
                    view?.showUserLimitActive(true)

                    val requested = totalTickets - marketplace.userTicketLimitRemaining!!
                    view?.showUserLimit(requested, marketplace.userTicketLimitRemaining!! + requested)
                }
                else -> {
                    view?.showPickerCount(createCountItems(totalTickets))
                    view?.showUserLimitActive(false)
                }
            }
        }

        val hasTicketSelected = selectedTickets > 0

        view?.showPicker(!hasTicketSelected, 0L) {
            view?.showInput(hasTicketSelected, false, INPUT_ANIM_DURATION)
            view?.showTicketSelected(hasTicketSelected)
            view?.showProceedToReview(hasTicketSelected)
            view?.showTicketCountSelected(selectedTickets)
            if (hasTicketSelected) view?.showInputAmount(inputAmount.toString())
        }
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
                        view?.showProceedToReviewEnabled(false)
                    }
                    timer?.start()
                }
                else -> view?.showTransactionClosesLessThen5Mins(false)
            }
            else -> {
                isTransactionClosed = true
                view?.showTransactionClosesLessThen5Mins(true)
                view?.showTransactionClosed()
                view?.showProceedToReviewEnabled(false)
            }
        }
    }

    internal fun shareEventClicked(context: Context) {
        GlideApp.with(context).asBitmap().load(event.defaultMedia?.url).intoBitmap { bitmap ->
            disposables += fileManager.getEventMediaUri(bitmap).subscribe({ uri ->
                view?.showEventSharing(uri, event.name!!, event.locationName!!, "$SHARE_URL${event.id}")
            }, { error ->
                Timber.e(error, "Error obtaining media uri from bitmap.")
            })
        }
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

    companion object {
        private const val SHARE_URL = "https://enovlab.com/events?eventId="
        private const val PICKER_ANIM_DURATION = 500L
        private const val INPUT_ANIM_DURATION = 700L
    }
}