package com.enovlab.yoop.ui.main.mytickets.requested

import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.enums.OfferStatus.*
import com.enovlab.yoop.data.entity.enums.OfferSubStatus.AUTO_PAYMENT_FAILED
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.OfferGroup
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.MarketplaceRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem.*
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RequestedViewModel
@Inject constructor(private val eventsRepository: EventsRepository,
                    private val marketplaceRepository: MarketplaceRepository) : StateViewModel<RequestedView>() {

    private var filter = Filter.ALL

    override fun start() {
        observeLocalUserEvents()
        loadUserEvents()
        updateFilter()
    }

    private fun loadUserEvents(refresh: Boolean = false) {
        when {
            refresh -> refresh { eventsRepository.loadUserEvents().toCompletable() }
            else -> load { eventsRepository.loadUserEvents().toCompletable() }
        }
    }

    private fun observeLocalUserEvents() {
        singleSubscription?.dispose()
        singleSubscription = eventsRepository.observeUserEvents()
            .observeOn(schedulers.disk)
            .map(::mapToAdapterItems)
            .observeOn(schedulers.main)
            .subscribe({ items ->
                when {
                    items.isNotEmpty() -> {
                        view?.showFilters(true)
                        view?.showEmptyEvents(false)
                    }
                    else -> {
                        view?.showFilters(filter != Filter.ALL)
                        view?.showEmptyEvents(filter == Filter.ALL)
                    }
                }
                view?.showRequestedItems(items)
            }, { error ->
                Timber.e(error)
            })
    }

    internal fun refresh() {
        loadUserEvents(true)
    }

    internal fun filterAllClicked() {
        filter = Filter.ALL
        observeLocalUserEvents()
        updateFilter()
    }

    internal fun filterYoopListClicked() {
        filter = Filter.YOOP_LIST
        observeLocalUserEvents()
        updateFilter()
    }

    internal fun filterOnSaleClicked() {
        filter = Filter.ON_SALE
        observeLocalUserEvents()
        updateFilter()
    }

    internal fun archive(ids: List<String>) {
        filter = Filter.ALL
        updateFilter()
        action {
            marketplaceRepository.archiveOffers(ids).doOnComplete {
                loadUserEvents()
            }
        }
    }

    private fun updateFilter() {
        view?.showAllTickets(filter == Filter.ALL)
        view?.showYoopListTickets(filter == Filter.YOOP_LIST)
        view?.showOnSaleTickets(filter == Filter.ON_SALE)
    }

    private fun filterType() = when (filter) {
        Filter.YOOP_LIST -> MarketplaceType.DRAW
        else -> MarketplaceType.AUCTION
    }

    private fun mapToAdapterItems(events: List<Event>): List<RequestedItem> {
        var filteredEvents = events
        if (filter != Filter.ALL) {
            val type = filterType()
            filteredEvents = filteredEvents.filter { it.marketplaceInfo!!.filter { it.type == type }.isNotEmpty() }
        }

        val items = mutableListOf<RequestedItem>()

        val currentDate = Date()
        for (event in filteredEvents) {
            val mediaUrl = event.defaultMedia?.url
            val name = event.shortName
            val date = event.date?.let { DATE_FORMAT.format(it) }
            val location = event.locationName
            val currency = currencySign(event.currency!!)

            var requestedItem: RequestedItem? = null
            val offers = mutableListOf<TransactionOfferItem>()

            val marketplaces = event.marketplaceInfo?.sortedBy { it.startDate }
            if (marketplaces == null || marketplaces.isEmpty()) continue

            for (marketplace in marketplaces) {
                val offerGroups = marketplace.offerGroups!!

                // active marketplace
                if (currentDate >= marketplace.startDate && currentDate <= marketplace.endDate) {
                    val activeOffers = offerGroups.filter {
                        it.offer != null && it.offer?.displayArchive == false && it.offer?.offerStatus == DEFAULT
                    }

                    if (activeOffers.isNotEmpty()) {
                        val endDate = marketplace.endDate?.let { DATE_FORMAT_PILL.format(it) }
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> {
                                offers.addAll(activeListOffers(activeOffers, currency))
                                requestedItem = ActiveListItem(event.id, name, mediaUrl, date, location, endDate)
                            }
                            MarketplaceType.AUCTION -> {
                                offers.addAll(activeOnSaleOffers(activeOffers, currency))
                                requestedItem = ActiveOnSaleItem(event.id, name, mediaUrl, date, location, endDate)
                            }
                            else -> { /* nothing */ }
                        }

                        break
                    }
                }

                // marketplace has ended
                if (currentDate > marketplace.endDate) {
                    // pending result offers
                    val pendingOffers = offerGroups.filter {
                        it.offer != null && it.offer?.displayArchive == false
                            && (it.offer?.offerStatus == DEFAULT || it.offer?.offerStatus == WON_PAYMENT_PROCESSING_PENDING)
                    }

                    // selected offers, no auto-processing
                    val selectedOffers = offerGroups.filter {
                        it.offer != null && it.offer?.displayArchive == false
                            && it.offer?.offerStatus == WON_MANUAL_PAYMENT_REQUIRED && it.offer?.offerSubStatus == null
                    }

                    // selected offers, payment failed
                    val failedPaymentOffers = offerGroups.filter {
                        it.offer != null && it.offer?.displayArchive == false
                            && it.offer?.offerStatus == WON_MANUAL_PAYMENT_REQUIRED
                            && it.offer?.offerSubStatus == AUTO_PAYMENT_FAILED
                    }

                    // merge all offers
                    if (failedPaymentOffers.isNotEmpty()) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> offers.addAll(paymentFailedListOffers(failedPaymentOffers))
                            MarketplaceType.AUCTION -> offers.addAll(paymentFailedOnSaleOffers(failedPaymentOffers, currency))
                            else -> { /* nothing */ }
                        }
                    }
                    if (selectedOffers.isNotEmpty()) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> offers.addAll(selectedListOffers(selectedOffers))
                            MarketplaceType.AUCTION -> offers.addAll(selectedOnSaleOffers(selectedOffers, currency))
                            else -> { /* nothing */ }
                        }
                    }
                    if (pendingOffers.isNotEmpty()) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> offers.addAll(pendingListOffers(pendingOffers))
                            MarketplaceType.AUCTION -> offers.addAll(pendingOnSaleOffers(pendingOffers, currency))
                            else -> { /* nothing */ }
                        }
                    }

                    // paid offers, but pending - search only if there some other offers are exists
                    if (pendingOffers.isNotEmpty() || selectedOffers.isNotEmpty() || failedPaymentOffers.isNotEmpty()) {
                        val paidOffers = offerGroups.filter {
                            it.offer != null && it.offer?.displayArchive == false && it.offer?.offerStatus == WON_PAYMENT_SUCCESSFUL
                        }
                        if (paidOffers.isNotEmpty()) {
                            when (marketplace.type) {
                                MarketplaceType.DRAW -> offers.addAll(paidPendingListOffers(paidOffers))
                                MarketplaceType.AUCTION -> offers.addAll(paidPendingOnSaleOffers(paidOffers, currency))
                                else -> { /* nothing */ }
                            }
                        }
                    }

                    // if there is only pending offers - create pending items, otherwise - action required items
                    if (pendingOffers.isNotEmpty() && selectedOffers.isEmpty() && failedPaymentOffers.isEmpty()) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> requestedItem = PendingListItem(event.id, name, mediaUrl, date, location)
                            MarketplaceType.AUCTION -> requestedItem = PendingOnSaleItem(event.id, name, mediaUrl, date, location)
                            else -> { /* nothing */ }
                        }
                        break
                    } else if (selectedOffers.isNotEmpty() || failedPaymentOffers.isNotEmpty()) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> requestedItem = ActionRequiredListItem(event.id, name, mediaUrl, date, location)
                            MarketplaceType.AUCTION -> requestedItem = ActionRequiredOnSaleItem(event.id, name, mediaUrl, date, location)
                            else -> { /* nothing */ }
                        }
                        break
                    }
                }
            }

            // if no active, pending offers - check if next marketplace is exists
            if (requestedItem == null && event.nextMarketplace != null) {
                when {
                    currentDate >= event.nextMarketplace!!.startDate -> {
                        val endDate = event.nextMarketplace!!.endDate.let { DATE_FORMAT_PILL.format(it) }
                        when (event.nextMarketplace!!.type) {
                            MarketplaceType.DRAW -> requestedItem = TryListItem(event.id, name, mediaUrl, date, location, endDate)
                            MarketplaceType.AUCTION -> requestedItem = TryOnSaleItem(event.id, name, mediaUrl, date, location, endDate)
                            else -> { /* nothing */ }
                        }
                    }
                    else -> {
                        val startDate = event.nextMarketplace!!.startDate.let { DATE_FORMAT_PILL.format(it) }
                        when (event.nextMarketplace!!.type) {
                            MarketplaceType.DRAW -> requestedItem = OpensListItem(event.id, name, mediaUrl, date, location, startDate)
                            MarketplaceType.AUCTION -> requestedItem = OpensOnSaleItem(event.id, name, mediaUrl, date, location, startDate)
                            else -> { /* nothing */ }
                        }
                    }
                }
            }

            // if no active, pending, next marketplace offers - closed
            if (requestedItem == null) {
                requestedItem = ClosedItem(event.id, name, mediaUrl, date, location)
            }

            // lost offers
            val lostOffers = mutableListOf<TransactionOfferItem>()
            marketplaces.filter { currentDate > it.endDate }.forEach {
                val offerGroups = it.offerGroups!!
                val lostOfferGroups = offerGroups.filter {
                    it.offer != null && it.offer?.displayArchive == false
                        && (it.offer?.offerStatus != WON_TOKEN_ASSIGNED || it.offer?.offerStatus != WON_PAYMENT_SUCCESSFUL)
                        && it.offer?.offerStatus == LOST && it.offer?.offerSubStatus != AUTO_PAYMENT_FAILED
                }

                if (lostOfferGroups.isNotEmpty()) {
                    when (it.type) {
                        MarketplaceType.DRAW -> lostOffers.addAll(lostListOffers(lostOfferGroups))
                        MarketplaceType.AUCTION -> lostOffers.addAll(lostOnSaleOffers(lostOfferGroups, currency))
                        else -> { /* nothing */ }
                    }
                }
            }

            if (offers.isNotEmpty() || lostOffers.isNotEmpty()) {
                requestedItem.offers = offers
                requestedItem.lostOffers = lostOffers
                items.add(requestedItem)
            }
        }

        return items
    }

    private fun activeListOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.ActiveListOfferItem(it.id, it.description, currency,
                it.offer?.numberOfTokens ?: 0, it.offer?.amount?.toInt() ?: 0)
        }
    }

    private fun activeOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.ActiveOnSaleOfferItem(it.id, it.description, currency,
                it.offer?.numberOfTokens ?: 0, it.offer?.amount?.toInt() ?: 0, it.offer?.chance)
        }.sortedByDescending { it.chance }
    }

    private fun lostListOffers(offerGroups: List<OfferGroup>): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.LostListOfferItem(it.offer?.id ?: "", it.description, it.offer?.numberOfTokens ?: 0)
        }
    }

    private fun lostOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.LostOnSaleOfferItem(it.offer?.id ?: "", it.description, currency,
                it.offer?.amount?.toInt() ?: 0,
                it.offer?.numberOfTokens ?: 0)
        }
    }

    private fun selectedListOffers(offerGroups: List<OfferGroup>): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.SelectedListOfferItem(it.id, it.description, it.offer?.numberOfTokens ?: 0, it.offer?.retryEndTime)
        }
    }

    private fun selectedOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.SelectedOnSaleOfferItem(it.id, it.description, currency,
                it.offer?.amount?.toInt() ?: 0, it.offer?.numberOfTokens ?: 0, it.offer?.retryEndTime)
        }
    }

    private fun pendingListOffers(offerGroups: List<OfferGroup>): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PendingListOfferItem(it.id, it.description, it.offer?.numberOfTokens ?: 0)
        }
    }

    private fun pendingOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PendingOnSaleOfferItem(it.id, it.description, currency,
                it.offer?.numberOfTokens ?: 0, it.offer?.amount?.toInt() ?: 0)
        }
    }

    private fun paymentFailedListOffers(offerGroups: List<OfferGroup>): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PaymentFailedListOfferItem(it.id, it.description,
                it.offer?.numberOfTokens ?: 0, it.offer?.retryEndTime)
        }
    }

    private fun paymentFailedOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PaymentFailedOnSaleOfferItem(it.id, it.description, currency,
                it.offer?.numberOfTokens ?: 0,
                it.offer?.amount?.toInt() ?: 0,
                it.offer?.retryEndTime)
        }
    }

    private fun paidPendingListOffers(offerGroups: List<OfferGroup>): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PaidPendingListOfferItem(it.id, it.description, it.offer?.numberOfTokens ?: 0)
        }
    }

    private fun paidPendingOnSaleOffers(offerGroups: List<OfferGroup>, currency: String): List<TransactionOfferItem> {
        return offerGroups.map {
            TransactionOfferItem.PaidPendingOnSaleOfferItem(it.id, it.description, currency,
                it.offer?.numberOfTokens ?: 0, it.offer?.amount?.toInt() ?: 0)
        }
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }

    enum class Filter {
        ALL, YOOP_LIST, ON_SALE
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("EEE, M/dd", Locale.getDefault())
        private val DATE_FORMAT_PILL = SimpleDateFormat("M/dd", Locale.getDefault())
    }
}