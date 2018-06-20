package com.enovlab.yoop.ui.transaction.details

import android.content.Context
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.enums.MarketplaceType.AUCTION
import com.enovlab.yoop.data.entity.enums.MarketplaceType.DRAW
import com.enovlab.yoop.data.entity.enums.OfferStatus
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.OfferGroup
import com.enovlab.yoop.data.entity.event.Performer
import com.enovlab.yoop.data.manager.FileManager
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.inject.GlideApp
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.transaction.details.adapter.OfferGroupItem
import com.enovlab.yoop.utils.ext.intoBitmap
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author vishaan
 */
class TransactionDetailsViewModel
@Inject constructor(private val repository: EventsRepository,
                    private val fileManager: FileManager) : StateViewModel<TransactionDetailsView>() {

    internal lateinit var id: String
    internal lateinit var type: MarketplaceType

    private var sorting = Sorting.MIN_ASK
    private lateinit var event: Event

    override fun start() {
        observeEvent()
        action { repository.loadEvent(id).toCompletable() }
    }

    internal fun offerGroupClicked(offerGroupId: String, hasUserActivity: Boolean) {
        when {
            hasUserActivity -> view?.showEditTransaction(id, type.name, offerGroupId)
            else -> view?.showCountSelection(id, type.name, offerGroupId)
        }
    }

    private fun observeEvent() {
        disposables += repository.observeEvent(id).subscribe({ event ->
            this.event = event

            view?.showEventName(event.shortName)
            view?.showEventDateLocation(DATE_FORMAT_HEADER.format(event.date), event.locationName)

            when (type) {
                DRAW -> view?.showPriceTypeList()
                else -> view?.showPriceTypeMinimumAsk()
            }

            performerPictureUrl(event.performers)?.let { url ->
                view?.showPerformerPictureUrl(url)
            }

            view?.showUserPictureUrl(event.userInfo?.photo)

            if (event.marketplaceInfo != null) {
                val marketplace = event.marketplaceInfo!!.find { it.type == type }
                if (marketplace == null) return@subscribe

                var offerGroups = marketplace.offerGroups!!.map {
                    mapToAdapterItem(it, currencySign(event.currency!!),
                        event.userInfo?.photo,
                        it.offer != null
                            && it.offer!!.offerStatus != OfferStatus.WON_TOKEN_ASSIGNED
                            && it.offer!!.offerStatus != OfferStatus.WON_PAYMENT_SUCCESSFUL,
                        when {
                            it.offer != null && it.offer!!.offerStatus == OfferStatus.WON_TOKEN_ASSIGNED -> it.offer?.numberOfTokens ?: 0
                            else -> 0
                        })
                }
                val multipleSelection = offerGroups.size > 1

                when {
                    multipleSelection && type == AUCTION -> {
                        when (sorting) {
                            Sorting.MIN_ASK -> {
                                offerGroups = offerGroups.sortedBy { (it as OfferGroupItem.OnSaleOfferGroup).minAskPrice }
                                view?.showSortingByMinAsk()
                            }
                            Sorting.AVG_OFFER -> {
                                offerGroups = offerGroups.sortedBy { (it as OfferGroupItem.OnSaleOfferGroup).averageOfferPrice }
                                view?.showSortingByAverageOffer()
                            }
                        }
                        view?.showSorting(true)
                    }
                    else -> {
                        view?.showSorting(false)
                    }
                }

                view?.showOfferGroups(offerGroups)

                view?.showSeatMap(event.seatMap?.url, isImageUrlSvg(event.seatMap?.url))

                view?.showSeatMapHeight()
            }
        }, { error ->
            Timber.e(error)
        })
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

    internal fun sortingClicked(offerGroups: List<OfferGroupItem>) {
        sorting = if (sorting == Sorting.MIN_ASK) Sorting.AVG_OFFER else Sorting.MIN_ASK

        when (sorting) {
            Sorting.MIN_ASK -> {
                view?.showSortingByMinAsk()
                view?.showOfferGroups(offerGroups.sortedBy { (it as OfferGroupItem.OnSaleOfferGroup).minAskPrice })
            }
            Sorting.AVG_OFFER -> {
                view?.showSortingByAverageOffer()
                view?.showOfferGroups(offerGroups.sortedBy { (it as OfferGroupItem.OnSaleOfferGroup).averageOfferPrice })
            }
        }
    }

    private fun performerPictureUrl(performers: List<Performer>?): String? {
        if (performers == null || performers.isEmpty()) return null
        return performers.first().defaultMedia?.url
    }

    private fun mapToAdapterItem(offerGroup: OfferGroup, currency: String,
                                 userPhoto: String?, hasUserActivity: Boolean, userTickets: Int) = when (type) {
        MarketplaceType.DRAW -> OfferGroupItem.ListOfferGroup(offerGroup.id, offerGroup.description, currency,
            if (hasUserActivity) userPhoto else null, hasUserActivity, userTickets,
            offerGroup.numberOfTokens ?: 0,
            offerGroup.reservePrice?.toInt() ?: 0)
        else -> OfferGroupItem.OnSaleOfferGroup(offerGroup.id, offerGroup.description, currency,
            if (hasUserActivity) userPhoto else null, hasUserActivity, userTickets,
            offerGroup.averageOfferPrice?.toInt() ?: 0,
            offerGroup.reservePrice?.toInt() ?: 0,
            offerGroup.demand,
            offerGroup.offer?.chance,
            offerGroup.minQualifyingPrice?.toInt())
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }

    private fun isImageUrlSvg(url: String?): Boolean {
        return url != null && url.endsWith(".svg")
    }

    private enum class Sorting {
        MIN_ASK, AVG_OFFER
    }

    companion object {
        private const val SHARE_URL = "https://enovlab.com/events?eventId="
        private val DATE_FORMAT_HEADER = SimpleDateFormat("M/d, ha", Locale.getDefault())
    }
}