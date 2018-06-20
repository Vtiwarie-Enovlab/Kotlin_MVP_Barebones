package com.enovlab.yoop.ui.event.landing

import android.net.Uri
import com.enovlab.yoop.data.entity.CalenderInfo
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.event.Timeline
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * Created by mtosk on 3/5/2018.
 */
interface EventLandingView : StateView {

    fun showVideoPlayer(player: SimpleExoPlayer)

    fun showEventName(name: String?)
    fun showEventDate(date: String?)
    fun showEventLocation(location: String?)
    fun showEventDateLocation(dateLocation: String)

    fun showEventAddress(address: String)
    fun showEventLocationName(location: String)
    fun showEventOnMap(latitude: Double, longitude: Double, uri: String)
    fun showLocationActive(active: Boolean)

    fun showEventTimeline(description: String?, timelines: List<Timeline>)
    fun showTimelineActive(active: Boolean)

    fun showTopSpace(isGoing: Boolean)
    fun showScrolledHeader(active: Boolean)
    fun showScrolledContentPlayerForegroundUpdate(update: Boolean)

    fun showVolumeEnabled(enabled: Boolean)

    fun showTransactionActive(active: Boolean)
    fun showTransaction(active: Boolean)
    fun showTransactionMoreEvents(active: Boolean)
    fun showOnSaleTransaction()
    fun showListTransaction()
    fun showOnSaleHighDemandTransaction()
    fun showOnSaleDemandExceedsSupplyTransaction()
    fun showListHighDemandTransaction()
    fun showTransactionPriceActive(active: Boolean)
    fun showTransactionNotification(active: Boolean)

    fun showTransactionLessThen24Hours(active: Boolean)

    fun showTransactionClosesDate(date: String)
    fun showTransactionClosesTomorrow()
    fun showTransactionClosesHours(hours: Int)
    fun showTransactionClosesMinutes(minutes: Int)
    fun showTransactionClosesSeconds(seconds: Int)

    fun showTransactionOpensDate(date: String)
    fun showTransactionOpensTomorrow()
    fun showTransactionOpensHours(hours: Int)
    fun showTransactionOpensMinutes(minutes: Int)
    fun showTransactionOpensSeconds(seconds: Int)

    fun showTransactionLowestPrice(reservePrice: Int, currency: String)
    fun showOnSaleMarketplaceActive(active: Boolean)
    fun showListMarketplaceActive(active: Boolean)
    fun showOnSaleMarketplaceDate(startDate: String, endDate: String)
    fun showListMarketplaceDate(startDate: String, endDate: String)
    fun showOnSaleMarketplaceLive(active: Boolean)
    fun showListMarketplaceLive(active: Boolean)
    fun showMarketplaceChances(chances: Int, eventPassed: Boolean)

    fun showPerformerPictureUrl(url: String)
    fun showUserPictureUrl(photo: String?)

    fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String)
    fun showAddEventToCalendar(calendarInfo: CalenderInfo)
    fun showEventAddedToCalendar()

    fun showTransactionDetails(id: String, type: String)
    fun showTransactionHistory(items: List<TransactionOfferItem>)
    fun showTransactionClosed()
    fun showTransactionHistoryActive(active: Boolean)
    fun showTransactionActionRequired()
    fun showTransactionPendingResults()
    fun showTransactionPendingAssignment()

    fun showMyRequestsActive(active: Boolean)
    fun showMyRequest(multiple: Boolean)
    fun showMyRequestActionRequired(active: Boolean)
    fun showMyOffersActive(active: Boolean)
    fun showMyOffersMultiple()
    fun showMyOffers(amount: Int, currency: String)
    fun showMyOffersChance(chance: Chance?)

    fun showGoingLoops()
    fun showUserGoing(active: Boolean)
    fun showTransactionEdit(id: String, type: String, offerGroupId: String)

    // going
    fun showGetMoreTickets(active: Boolean)
    fun showGetMoreListTickets()
    fun showGetMoreOnSaleTickets()
    fun showAssignedTokensTitle(assigned: Int, total: Int)
    fun showTokenAssignments(active: Boolean)
    fun showMarketplaceInfo(active: Boolean)
    fun showTokenAssignmentItems(items: List<TokenItem>)
    fun showTicketDetails(id: String, ticketId: String)
    fun showTransactionSheetListener(active: Boolean)

    fun showTransactionFix(eventId: String, marketplaceType: String, offerGroupId: String)
    fun showTransactionClaim(eventId: String, marketplaceType: String, offerGroupId: String)
}