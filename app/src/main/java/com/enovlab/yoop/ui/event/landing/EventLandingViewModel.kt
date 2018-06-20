package com.enovlab.yoop.ui.event.landing

import android.content.Context
import android.net.Uri
import com.enovlab.yoop.data.entity.CalenderInfo
import com.enovlab.yoop.data.entity.enums.*
import com.enovlab.yoop.data.entity.enums.OfferStatus.*
import com.enovlab.yoop.data.entity.enums.OfferSubStatus.AUTO_PAYMENT_FAILED
import com.enovlab.yoop.data.entity.event.*
import com.enovlab.yoop.data.entity.user.UserInfo
import com.enovlab.yoop.data.manager.DeepLinkManager
import com.enovlab.yoop.data.manager.FileManager
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.inject.GlideApp
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem
import com.enovlab.yoop.utils.CustomTimer
import com.enovlab.yoop.utils.GeoUtils
import com.enovlab.yoop.utils.VolumeObserver
import com.enovlab.yoop.utils.ext.intoBitmap
import com.enovlab.yoop.utils.ext.isLessThan24HoursLeft
import com.enovlab.yoop.utils.ext.isLessThan48HoursLeft
import com.enovlab.yoop.utils.ext.plusAssign
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class EventLandingViewModel
@Inject constructor(private val repository: EventsRepository,
                    private val fileManager: FileManager,
                    context: Context) : StateViewModel<EventLandingView>() {

    internal lateinit var id: String

    private var player: SimpleExoPlayer? = null
    private var isMediaSourceSet = false
    private var isScrolled = false
    private var singleTimeData = AtomicBoolean(false)
    private var timer: CustomTimer? = null
    private lateinit var event: Event

    private val volumeObserver = VolumeObserver(context)

    init {
        initPlayer(context)
        volumeObserver.listener = { view?.showVolumeEnabled(it) }
    }

    override fun start() {
        observeEvent(id)
        action { repository.loadEvent(id).toCompletable() }

        volumeObserver.updateVolume()

        view?.showVideoPlayer(player!!)
        playVideo()
    }

    override fun stop() {
        super.stop()
        pauseVideo()
        timer?.cancel()
    }

    override fun destroy() {
        super.destroy()
        releasePlayer()
    }

    private fun observeEvent(id: String) {
        disposables += repository.observeEvent(id).subscribe({ event ->
            this.event = event

            val hasAssignedTokens = (event.tokenInfo != null && event.tokenInfo!!.isNotEmpty())
                || (event.assigneeTokenInfo != null && event.assigneeTokenInfo!!.isNotEmpty())

            //space from top
            if (!singleTimeData.getAndSet(true)) {
                view?.showTopSpace(hasAssignedTokens)
            }

            //media setup
            eventVideoUrl(event.media)?.let { url ->
                setMediaSource(url)
            }
            playVideo()

            //pictures setup
            performerPictureUrl(event.performers)?.let { url ->
                view?.showPerformerPictureUrl(url)
            }
            view?.showUserPictureUrl(event.userInfo?.photo)
            if (hasAssignedTokens) view?.showGoingLoops()
            view?.showUserGoing(hasAssignedTokens)

            view?.showEventName(event.shortName)
            view?.showEventDate(getDateFormat(event.timeZone).format(event.date))
            view?.showEventLocation(event.locationName)
            view?.showEventDateLocation("${DATE_FORMAT_HEADER.format(event.date)} • ${event.locationName}")

            if (event.marketplaceInfo != null) {
                setupTransactionDetails()

                if (hasAssignedTokens) {
                    view?.showTokenAssignments(true)
                    view?.showMarketplaceInfo(false)
                    setupTokenAssignment(event.tokenInfo, event.assigneeTokenInfo, event.userInfo!!)
                } else {
                    view?.showMarketplaceInfo(true)
                    view?.showTokenAssignments(false)
                    setupMarketplaceInfo(event.marketplaceInfo!!)
                }

                if (event.timelines != null) {
                    view?.showEventTimeline(event.shortDescription, event.timelines!!.sortedBy { it.rank })
                    view?.showTimelineActive(true)
                }

                view?.showEventAddress("${event.addressLine1}\n" +
                    "${event.cityName}, ${event.geoRegionAbbreviation} ${event.zipCode}")
                view?.showEventLocationName(event.locationName!!)
                view?.showLocationActive(true)

                if (event.locationLatitude != null && event.locationLongitude != null) {
                    view?.showEventOnMap(event.locationLatitude!!, event.locationLongitude!!,
                        createGeoUri(event.locationLatitude!!, event.locationLongitude!!, event.shortName!!))
                }
            }
        }, { error ->
            Timber.e(error)
        })
    }

    private fun setupTransactionDetails() {
        val marketplaces = event.marketplaceInfo!!.sortedBy { it.startDate }
        val currency = currencySign(event.currency!!)
        val currentDate = Date()
        val hasAssignedTokens = (event.tokenInfo != null && event.tokenInfo!!.isNotEmpty())
            || (event.assigneeTokenInfo != null && event.assigneeTokenInfo!!.isNotEmpty())

        for (marketplace in marketplaces) {
            val offerGroups = marketplace.offerGroups!!

            // active marketplace
            if (currentDate >= marketplace.startDate && currentDate <= marketplace.endDate) {

                view?.showTransactionActive(true)
                setupTransactionType(marketplace)
                setupTransactionDateTime(marketplace.endDate!!, false)
                view?.showTransaction(true)
                view?.showTransactionNotification(false)
                view?.showTransactionMoreEvents(false)

                val activeOffers = offerGroups.filter {
                    it.offer != null && it.offer?.displayArchive == false && it.offer?.offerStatus == DEFAULT
                }

                if (activeOffers.isEmpty()) {
                    val offerGroup = marketplace.offerGroups!!.sortedBy { it.reservePrice }.first()
                    view?.showTransactionLowestPrice(offerGroup.reservePrice?.toInt() ?: 0, currency)
                    view?.showTransactionPriceActive(true)

                    view?.showTransactionHistoryActive(false)
                    view?.showMyRequestsActive(false)
                    view?.showMyOffersActive(false)
                    view?.showTransactionSheetListener(false)

                    if (hasAssignedTokens) {
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> view?.showGetMoreListTickets()
                            MarketplaceType.AUCTION -> view?.showGetMoreOnSaleTickets()
                            else -> { /* nothing */ }
                        }
                        view?.showGetMoreTickets(true)
                        view?.showTransaction(false)
                    }
                } else {
                    // setup transaction button
                    val multiple = activeOffers.size > 1
                    when (marketplace.type) {
                        MarketplaceType.DRAW -> setupTransactionListButton(multiple, false)
                        MarketplaceType.AUCTION -> setupTransactionOnSaleButton(findWorstOffer(activeOffers), currency, multiple, false)
                        else -> { /* nothing */ }
                    }

                    val offers = mutableListOf<TransactionOfferItem>()
                    val reachedLimit = marketplace.userTicketLimitRemaining ?: 0 == 0
                    when (marketplace.type) {
                        MarketplaceType.DRAW -> {
                            offers.addAll(activeListOffers(activeOffers, currency))
                            if (!reachedLimit) offers.add(TransactionOfferItem.ActiveListNewOfferItem)
                        }
                        MarketplaceType.AUCTION -> {
                            offers.addAll(activeOnSaleOffers(activeOffers, currency))
                            if (!reachedLimit) offers.add(TransactionOfferItem.ActiveOnSaleNewOfferItem)
                        }
                        else -> { /* nothing */ }
                    }

                    view?.showTransactionHistory(offers)

                    view?.showTransactionHistoryActive(true)
                    view?.showTransactionPriceActive(false)
                }

                return
            }

            // marketplace has ended
            if (!hasAssignedTokens && currentDate > marketplace.endDate) {
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

                // paid offers
                val paidOffers = offerGroups.filter {
                    it.offer != null && it.offer?.displayArchive == false && it.offer?.offerStatus == WON_PAYMENT_SUCCESSFUL
                }

                val offers = mutableListOf<TransactionOfferItem>()

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
                if (paidOffers.isNotEmpty()) {
                    when (marketplace.type) {
                        MarketplaceType.DRAW -> offers.addAll(paidPendingListOffers(paidOffers))
                        MarketplaceType.AUCTION -> offers.addAll(paidPendingOnSaleOffers(paidOffers, currency))
                        else -> { /* nothing */ }
                    }
                }

                if (offers.isNotEmpty()) {
                    view?.showTransactionActive(true)

                    if (selectedOffers.isNotEmpty() || failedPaymentOffers.isNotEmpty()) {
                        // setup transaction button
                        val actionOffers = mutableListOf<OfferGroup>().apply {
                            addAll(selectedOffers)
                            addAll(failedPaymentOffers)
                        }
                        val multiple = offers.size > 1
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> setupTransactionListButton(multiple, true)
                            MarketplaceType.AUCTION -> setupTransactionOnSaleButton(findWorstOffer(actionOffers), currency, multiple, true)
                            else -> { /* nothing */ }
                        }
                        view?.showTransactionActionRequired()
                        view?.showTransactionHistoryActive(true)
                    } else if (pendingOffers.isNotEmpty()) {
                        // setup transaction button
                        val multiple = offers.size > 1
                        when (marketplace.type) {
                            MarketplaceType.DRAW -> setupTransactionListButton(multiple, false)
                            MarketplaceType.AUCTION -> setupTransactionOnSaleButton(findWorstOffer(pendingOffers), currency, multiple, false)
                            else -> { /* nothing */ }
                        }
                        view?.showTransactionPendingResults()
                        view?.showTransactionHistoryActive(true)
                    } else if (paidOffers.isNotEmpty()) {
                        view?.showTransactionPendingAssignment()
                        view?.showTransactionHistoryActive(false)
                        view?.showMyRequestsActive(false)
                        view?.showMyOffersActive(false)
                        view?.showTransactionSheetListener(false)
                    }

                    setupTransactionType(marketplace)
                    view?.showTransaction(true)
                    view?.showTransactionNotification(false)
                    view?.showTransactionMoreEvents(false)
                    view?.showTransactionPriceActive(false)
                    view?.showTransactionHistory(offers)

                    return
                }
            }

            // check for the next marketplace
            if (currentDate < marketplace.startDate) {
                setupTransactionType(marketplace)
                setupTransactionDateTime(marketplace.startDate!!, true)
                view?.showTransactionActive(true)
                view?.showTransaction(true)
                view?.showTransactionNotification(true)
                view?.showTransactionPriceActive(false)
                view?.showMyOffersActive(false)
                view?.showMyRequestsActive(false)
                view?.showTransactionMoreEvents(false)
                view?.showGetMoreTickets(false)
                view?.showTransactionSheetListener(false)

                return
            }

            setupTransactionType(marketplace)
            view?.showTransactionActive(false)
            view?.showTransaction(!hasAssignedTokens)
            view?.showTransactionClosed()
            view?.showTransactionMoreEvents(!hasAssignedTokens)
            view?.showTransactionNotification(false)
            view?.showTransactionPriceActive(false)
            view?.showMyOffersActive(false)
            view?.showMyRequestsActive(false)
            view?.showTransactionSheetListener(false)
        }
    }

    private fun setupTransactionType(marketplace: MarketplaceInfo) {
        when (marketplace.type) {
            MarketplaceType.AUCTION -> {
                when (marketplace.demand) {
                    Demand.HIGH -> view?.showOnSaleHighDemandTransaction()
                    Demand.DEMAND_EXCEEDS_SUPPLY -> view?.showOnSaleDemandExceedsSupplyTransaction()
                    else -> view?.showOnSaleTransaction()
                }
            }
            MarketplaceType.DRAW -> {
                when (marketplace.demand) {
                    Demand.HIGH -> view?.showListHighDemandTransaction()
                    else -> view?.showListTransaction()
                }
            }
        }
    }

    private fun setupTransactionDateTime(dateTime: Date, opens: Boolean) {
        when {
            dateTime.isLessThan24HoursLeft() -> {
                timer = CustomTimer(dateTime)
                timer?.tickListener = {
                    when {
                        it.hours > 0 -> when {
                            opens -> view?.showTransactionOpensHours(it.hours)
                            else -> view?.showTransactionClosesHours(it.hours)
                        }
                        it.minutes > 0 -> when {
                            opens -> view?.showTransactionOpensMinutes(it.minutes)
                            else -> view?.showTransactionClosesMinutes(it.minutes)
                        }
                        else -> when {
                            opens -> view?.showTransactionOpensSeconds(it.seconds)
                            else -> view?.showTransactionClosesSeconds(it.seconds)
                        }
                    }
                }
                timer?.finishListener = ::setupTransactionDetails
                timer?.start()

                view?.showTransactionLessThen24Hours(true)
            }
            dateTime.isLessThan48HoursLeft() -> {
                when {
                    opens -> view?.showTransactionOpensTomorrow()
                    else -> view?.showTransactionClosesTomorrow()
                }
                view?.showTransactionLessThen24Hours(true)
            }
            else -> {
                when {
                    opens -> view?.showTransactionOpensDate(DATE_FORMAT_TRANSACTION.format(dateTime))
                    else -> view?.showTransactionClosesDate(DATE_FORMAT_TRANSACTION.format(dateTime))
                }
                view?.showTransactionLessThen24Hours(false)
            }
        }
    }

    private fun setupTransactionListButton(multiple: Boolean, actionRequired: Boolean) {
        view?.showMyRequestsActive(true)
        view?.showMyRequest(multiple)
        view?.showMyRequestActionRequired(actionRequired)
        view?.showMyOffersActive(false)
        view?.showTransactionSheetListener(true)
    }

    private fun setupTransactionOnSaleButton(worstOffer: Offer, currency: String, multiple: Boolean, actionRequired: Boolean) {
        view?.showMyOffersActive(true)
        when {
            multiple -> view?.showMyOffersMultiple()
            else -> view?.showMyOffers(worstOffer.amount?.toInt() ?: 0, currency)
        }
        when {
            actionRequired -> view?.showMyOffersChance(Chance.NEGLIGIBLE)
            else -> view?.showMyOffersChance(worstOffer.chance)
        }
        view?.showMyRequestsActive(false)
        view?.showTransactionSheetListener(true)
    }

    private fun setupMarketplaceInfo(marketplaceInfo: List<MarketplaceInfo>) {
        val currentDate = Date()

        var chances = 0
        marketplaceInfo.forEach {
            when (it.type) {
                MarketplaceType.AUCTION -> {
                    view?.showOnSaleMarketplaceActive(true)
                    view?.showOnSaleMarketplaceDate(
                        DATE_FORMAT_TRANSACTION.format(it.startDate), DATE_FORMAT_TRANSACTION.format(it.endDate))
                    view?.showOnSaleMarketplaceLive(currentDate >= it.startDate && currentDate <= it.endDate)
                }
                MarketplaceType.DRAW -> {
                    view?.showListMarketplaceActive(true)
                    view?.showListMarketplaceDate(
                        DATE_FORMAT_TRANSACTION.format(it.startDate), DATE_FORMAT_TRANSACTION.format(it.endDate))
                    view?.showListMarketplaceLive(currentDate >= it.startDate && currentDate <= it.endDate)
                }
            }

            if (currentDate <= it.endDate) chances++
        }

        val eventPassed = chances == 0
        view?.showMarketplaceChances(if (!eventPassed) chances else marketplaceInfo.size, eventPassed)
    }

    private fun setupTokenAssignment(tokenInfo: List<TokenInfo>?,
                                     assigneeTokenInfo: List<TokenInfo>?,
                                     user: UserInfo) {

        val tokens = mutableListOf<TokenInfo>().apply {
            if (tokenInfo != null && tokenInfo.isNotEmpty()) addAll(tokenInfo)
            if (assigneeTokenInfo != null && assigneeTokenInfo.isNotEmpty()) addAll(assigneeTokenInfo)
        }

        val items = mutableListOf<TokenItem>()

        // setup user's own token
        // get first token from the assignment
        val userToken = tokens.find { it.selfAssigned == true }
        if (userToken != null) {
            items.add(createUserTokenItem(userToken, user))
            tokens.remove(userToken)
        }

        // setup rest tokens
        tokens.forEach { token ->
            items.add(createAssigneeTokenItem(token))
        }

        val unassigned = items.filter { it is TokenItem.UnassignedTokenItem }.size
        val total = items.size
        view?.showAssignedTokensTitle(total - unassigned, total)

        view?.showTokenAssignmentItems(items)
    }

    internal fun onSoundClicked() {
        volumeObserver.updateMuteSettings()
    }

    internal fun keyVolumeChanged() {
        volumeObserver.updateVolume()
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

    internal fun transactionNotificationClicked() {
        if (event.marketplaceInfo != null && event.marketplaceInfo?.isNotEmpty() == true) {
            val marketplace = findSuitableMarketplace(event.marketplaceInfo!!)
            view?.showAddEventToCalendar(createCalendarInfo(event, marketplace?.startDate, marketplace?.endDate))
        }
    }

    internal fun transactionDetailsClicked() {
        if (event.marketplaceInfo != null && event.marketplaceInfo?.isNotEmpty() == true) {
            val marketplace = findSuitableMarketplace(event.marketplaceInfo!!)
            if (marketplace != null)
                view?.showTransactionDetails(id, marketplace.type!!.name)
        }
    }

    internal fun transactionEditClicked(offerGroupId: String) {
        if (event.marketplaceInfo != null && event.marketplaceInfo?.isNotEmpty() == true) {
            val marketplace = findSuitableMarketplace(event.marketplaceInfo!!)
            if (marketplace != null)
                view?.showTransactionEdit(id, marketplace.type!!.name, offerGroupId)
        }
    }

    internal fun transactionFixPaymentClicked(item: TransactionOfferItem) {
        val type = when (item) {
            is TransactionOfferItem.PaymentFailedListOfferItem -> MarketplaceType.DRAW
            else -> MarketplaceType.AUCTION
        }
        view?.showTransactionFix(event.id, type.name, item.id)
    }

    internal fun transactionClaimClicked(item: TransactionOfferItem) {
        val type = when (item) {
            is TransactionOfferItem.SelectedListOfferItem -> MarketplaceType.DRAW
            else -> MarketplaceType.AUCTION
        }
        view?.showTransactionClaim(event.id, type.name, item.id)
    }

    internal fun checkCalendarDeepLink(deepLink: String?) {
        if (deepLink != null) {
            val eventId = DeepLinkManager.eventId(deepLink)
            if (eventId == id) {
                view?.showEventAddedToCalendar()
            }
        }
    }

    internal fun contentScrolled(y: Int) {
        val currentScroll = y > 0
        if (currentScroll != isScrolled) {
            isScrolled = currentScroll
            when {
                isScrolled -> pauseVideo()
                else -> playVideo()
            }
            view?.showScrolledHeader(isScrolled)
            view?.showScrolledContentPlayerForegroundUpdate(isScrolled)
        }
    }

    internal fun transactionsExpanded(expanded: Boolean) {
        if (!isScrolled) when {
            expanded -> pauseVideo()
            else -> playVideo()
        }
    }

    internal fun assignmentClicked(ticketId: String) {
        view?.showTicketDetails(id, ticketId)
    }

    private fun createCalendarInfo(event: Event, startDate: Date?, endDate: Date?): CalenderInfo {
        return CalenderInfo(
            event.id,
            "$SHARE_URL${event.id}",
            event.name!!,
            startDate, endDate,
            event.shortDescription,
            GeoUtils.getFullAddress(event.addressLine1, event.addressLine2,
                event.cityName, event.geoRegionName, event.zipCode)
        )
    }

    private fun findSuitableMarketplace(marketplaces: List<MarketplaceInfo>): MarketplaceInfo? {
        val currentDate = Date()

        var marketplace: MarketplaceInfo? = null

        val sortedMarketplaces = marketplaces.sortedBy { it.startDate }
        val activeMarketplace = sortedMarketplaces.find { currentDate >= it.startDate && currentDate <= it.endDate }
        when {
            activeMarketplace != null -> marketplace = activeMarketplace
            else -> {
                val earliestMarketplace = sortedMarketplaces.find { currentDate <= it.startDate }
                if (earliestMarketplace != null && currentDate <= earliestMarketplace.endDate) {
                    marketplace = earliestMarketplace
                }
            }
        }

        return marketplace
    }

    private fun eventVideoUrl(media: List<EventMedia>?): String? {
        if (media == null || media.isEmpty()) return null
        return media.find { it.type == EventMediaType.VIDEO }?.url
    }

    private fun performerPictureUrl(performers: List<Performer>?): String? {
        if (performers == null || performers.isEmpty()) return null
        return performers.first().defaultMedia?.url
    }

    private fun initPlayer(context: Context) {
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            DefaultTrackSelector(), DefaultLoadControl())
        player?.repeatMode = Player.REPEAT_MODE_ALL
    }

    private fun setMediaSource(url: String) {
        if (!isMediaSourceSet) {
            val mediaSource = ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(USER_AGENT))
                .createMediaSource(Uri.parse(url))
            player?.prepare(mediaSource, false, false)
            isMediaSourceSet = true
        }
    }

    private fun playVideo() {
        if (isMediaSourceSet && !isScrolled && player?.playWhenReady == false)
            player?.playWhenReady = true
    }

    private fun pauseVideo() {
        player?.playWhenReady = false
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun createGeoUri(lat: Double, lon: Double, name: String): String {
        return "geo:<$lat>,<$lon>?q=<$lat>,<$lon>($name)&z=16"
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }

    private fun findWorstOffer(offerGroups: List<OfferGroup>): Offer {
        return offerGroups.sortedBy { it.offer!!.chance }.last().offer!!
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

    private fun createUserTokenItem(token: TokenInfo, user: UserInfo): TokenItem {
        return when {
            user.photoVerified == true -> TokenItem.UserVerifiedTokenItem(token.id, token.section!!, user.photo!!)
            user.eventReady == true -> TokenItem.UserEventReadyTokenItem(token.id, token.section!!, user.photo!!)
            else -> TokenItem.UserNoPhotoTokenItem(token.id, token.section!!)
        }
    }

    private fun createAssigneeTokenItem(token: TokenInfo): TokenItem {
        val assignment = token.tokenAssignment

        // count as unassigned token
        if (assignment == null || assignment.assignmentStatus?.unassigned() == true) {
            return TokenItem.UnassignedTokenItem(token.id, token.section!!)
        }

        // pending accepting
        if (assignment.assignmentStatus == AssignmentStatus.PENDING) {
            return TokenItem.AssigneePendingTokenItem(token.id, token.section!!, assignment.email!!)
        }

        // token accepted
        return when {
            assignment.photoVerified == true -> TokenItem.AssigneeVerifiedTokenItem(token.id, token.section!!, assignment.firstName!!, assignment.photo!!)
            assignment.eventReady == true -> TokenItem.AssigneeEventReadyTokenItem(token.id, token.section!!, assignment.firstName!!, assignment.photo!!)
            else -> TokenItem.AssigneeNoPhotoTokenItem(token.id, token.section!!, assignment.firstName!!)
        }
    }

    companion object {
        private val DATE_FORMAT_TRANSACTION = SimpleDateFormat("M/d", Locale.getDefault())
        private val DATE_FORMAT_HEADER = SimpleDateFormat("M/d, ha", Locale.getDefault())
        private const val SHARE_URL = "https://enovlab.com/events?eventId="
        private const val USER_AGENT = "exoplayer-yoop"

        private fun getDateFormat(timeZone: String?): SimpleDateFormat {
            val timezontString = TimeZone.getTimeZone(timeZone)
                    .getDisplayName(false, TimeZone.SHORT)
            val dateString = "EEEE, M/d, ha '$timezontString'"
            return SimpleDateFormat(dateString, Locale.getDefault())
        }
    }
}