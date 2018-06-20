package com.enovlab.yoop.ui.event.landing

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.SimpleItemAnimator
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.CalenderInfo
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.event.Timeline
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.base.state.StateFragment
import com.enovlab.yoop.ui.event.EventNavigator
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.event.landing.adapter.TokenItemAdapter
import com.enovlab.yoop.ui.transaction.adapter.TransactionOfferItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersAdapter
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersItemDecoration
import com.enovlab.yoop.utils.ext.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_event_landing.*
import kotlinx.android.synthetic.main.layout_event_landing_app_bar.*
import kotlinx.android.synthetic.main.layout_event_landing_app_bar_scrolled.*
import kotlinx.android.synthetic.main.layout_event_landing_container_transactions.*
import kotlinx.android.synthetic.main.layout_event_landing_content.*
import kotlinx.android.synthetic.main.layout_event_landing_content_details.*
import kotlinx.android.synthetic.main.layout_event_landing_content_location.*
import kotlinx.android.synthetic.main.layout_event_landing_content_marketplace.*
import kotlinx.android.synthetic.main.layout_event_landing_content_timeline.*
import kotlinx.android.synthetic.main.layout_event_landing_transactions_sheet.*

/**
 * Created by mtosk on 3/5/2018.
 */
class EventLandingFragment : StateFragment<EventLandingView, EventLandingViewModel>(), EventLandingView {
    override val vmClass = EventLandingViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    lateinit var navigator: EventNavigator
    private val adapter by lazy { TransactionOffersAdapter() }
    private val assignmentAdapter by lazy { TokenItemAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        navigator = ViewModelProvider(activity!!, viewModelFactory)
                .get(EventNavigator::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sound.setOnClickListener { viewModel.onSoundClicked() }
        View.OnClickListener { navigator.navigateBack.go(true) }.applyToViews(back, back_scrolled)
        View.OnClickListener { viewModel.shareEventClicked(context!!) }.applyToViews(share, share_scrolled)

        player_view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
        player_view.setShutterBackgroundColor(Color.TRANSPARENT)

        transaction_more_events.setOnClickListener { navigator.navigateToDiscover.go() }
        transaction_notification.setOnClickListener {
            when {
                context!!.requiresCalendarPermission() -> {
                    requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR), RC_CALENDAR_PERMISSION)
                }
                else -> viewModel.transactionNotificationClicked()
            }
        }

        View.OnClickListener { viewModel.transactionDetailsClicked() }
            .applyToViews(transaction_price, event_more_tickets)

        val transactionsSheet = BottomSheetBehavior.from(transactions_sheet)

        View.OnClickListener {
            when (transactionsSheet.state) {
                BottomSheetBehavior.STATE_EXPANDED -> transactionsSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                else -> transactionsSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }.applyToViews(transaction_my_request, transaction_my_offer)

        overlay.setOnTouchListener{ v, _ ->
            if (v.isVisible) transactionsSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            false
        }

        adapter.listenerEdit = { viewModel.transactionEditClicked(it.id)}
        adapter.listenerAddNew = { viewModel.transactionDetailsClicked()  }
        adapter.listenerFix = { viewModel.transactionFixPaymentClicked(it) }
        adapter.listenerClaim = { viewModel.transactionClaimClicked(it) }
        transaction_list.adapter = adapter
        transaction_list.addItemDecoration(TransactionOffersItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small), true))
        (transaction_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        (scroll_view as NestedScrollView).setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            viewModel.contentScrolled(scrollY)
        })

        view.doOnLayout {
            player_view_overlay.layoutParams = player_view_overlay.layoutParams.apply {
                height = it.height
            }
        }

        assignmentAdapter.listener = { viewModel.assignmentClicked(it.id) }
        event_assignments_list.adapter = assignmentAdapter
        event_assignments_list.addItemDecoration(TransactionOffersItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_CALENDAR_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } )) {
                viewModel.transactionNotificationClicked()
            }
        }
    }

    override fun showVideoPlayer(player: SimpleExoPlayer) {
        player_view.player = player
    }

    override fun showVolumeEnabled(enabled: Boolean) {
        sound.setImageResource(if (enabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
    }

    fun dispatchKeyEvent(event: KeyEvent) {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            viewModel.keyVolumeChanged()
        }
    }

    override fun showEventName(name: String?) {
        event_name.text = name
        title.text = name
    }

    override fun showEventDate(date: String?) {
        event_date.text = date
    }

    override fun showEventLocation(location: String?) {
        event_location.text = location
    }

    override fun showEventDateLocation(dateLocation: String) {
        event_location_date.text = dateLocation
    }

    override fun showEventTimeline(description: String?, timelines: List<Timeline>) {
        event_description.text = description
        event_timeline.bindTimelines(timelines)
    }

    override fun showEventAddress(address: String) {
        event_address.text = address
    }

    override fun showEventLocationName(location: String) {
        event_location_name.text = location
    }

    override fun showEventOnMap(latitude: Double, longitude: Double, uri: String) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.event_map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            map.uiSettings.isMapToolbarEnabled = false
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context!!, R.raw.map_style))

            val latLng = LatLng(latitude, longitude)
            map.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(context!!.vectorToBitmap(R.drawable.ic_location_accent_32dp)))
                .position(latLng))

            map.setPadding(0, 0, 0, container_event_location.height)
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }

        open_map.setOnClickListener { navigator.navigateToGoogleMapsApp.go(uri) }
    }

    override fun showTopSpace(isGoing: Boolean) {
        container_event_content_details.doOnLayout {
            var begin = view!!.height - resources.getDimensionPixelSize(R.dimen.landing_transactions_height) - it.height
            begin = begin - resources.getDimensionPixelSize(R.dimen.margin_large) - (title_marketplace.height / 2)
            if (isGoing) { // decrease top space by 13%
                begin -= (begin / 100 * 13)
            }

            guideline_landing_space.setGuidelineBegin(begin)
        }
    }

    override fun showTimelineActive(active: Boolean) {
        container_event_content_timeline.isVisible = active
    }

    override fun showLocationActive(active: Boolean) {
        container_event_content_location.isVisible = active
    }

    override fun showScrolledHeader(active: Boolean) {
        app_bar_scrolled.animate()
            .alpha(if (active) 1f else 0f)
            .translationY(if (active) 0f else resources.getDimension(R.dimen.landing_app_bar_scroll_translation))
            .duration = ANIMATION_DURATION
    }

    override fun showScrolledContentPlayerForegroundUpdate(update: Boolean) {
        val colorStart = ContextCompat.getColor(context!!, android.R.color.transparent)
        val colorEnd = ContextCompat.getColor(context!!, R.color.color_black_alpha_30)
        val colorFrom = if (update) colorStart else colorEnd
        val colorTo = if (update) colorEnd else colorStart

        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                player_view.foreground = ColorDrawable(it.animatedValue as Int)
            }
            start()
        }
    }

    override fun showTransactionActive(active: Boolean) {
        val color = ContextCompat.getColor(context!!, if (active) R.color.color_white else R.color.color_white_alpha_50)
        transaction_name.setTextColor(color)
        transaction_date.setTextColor(color)
        transaction_icon.alpha = if (active) 1f else 0.5f
    }

    override fun showTransactionMoreEvents(active: Boolean) {
        transaction_more_events.isVisible = active
    }

    override fun showTransaction(active: Boolean) {
        transactions_sheet.isVisible = active
        container_event_landing_content.updatePadding(bottom = when {
            active -> resources.getDimensionPixelSize(R.dimen.landing_transactions_height)
            else -> 0
        })
    }

    override fun showTransactionClosed() {
        transaction_date.setText(R.string.event_landing_transaction_closed)
    }

    override fun showTransactionPriceActive(active: Boolean) {
        transaction_price.isVisible = active
    }

    override fun showTransactionLowestPrice(reservePrice: Int, currency: String) {
        transaction_price.setPrice(reservePrice, currency)
    }

    override fun showMyRequestsActive(active: Boolean) {
        transaction_my_request.isVisible = active
    }

    override fun showTransactionNotification(active: Boolean) {
        transaction_notification.isVisible = active
    }

    override fun showOnSaleTransaction() {
        transaction_icon.setImageResource(R.drawable.ic_onsale_white_26dp)
        transaction_name.setText(R.string.event_landing_on_sale)
    }

    override fun showListTransaction() {
        transaction_icon.setImageResource(R.drawable.ic_list_white_26dp)
        transaction_name.setText(R.string.event_landing_yoop_list)
    }

    override fun showOnSaleHighDemandTransaction() {
        transaction_icon.setImageResource(R.drawable.ic_onsale_white_26dp)
        transaction_name.setText(R.string.event_landing_on_sale_high_demand)
    }

    override fun showOnSaleDemandExceedsSupplyTransaction() {
        transaction_icon.setImageResource(R.drawable.ic_onsale_white_26dp)
        transaction_name.setText(R.string.event_landing_on_sale_demand_exceeds_supply)
    }

    override fun showListHighDemandTransaction() {
        transaction_icon.setImageResource(R.drawable.ic_list_white_26dp)
        transaction_name.setText(R.string.event_landing_yoop_list_high_demand)
    }

    override fun showTransactionClosesDate(date: String) {
        transaction_date.text = getString(R.string.event_landing_closes, date)
    }

    override fun showTransactionClosesTomorrow() {
        transaction_date.text = getString(R.string.event_landing_closes_tomorrow)
    }

    override fun showTransactionClosesHours(hours: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_closes_in_hours, hours, hours)
    }

    override fun showTransactionClosesMinutes(minutes: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_closes_in_minutes, minutes, minutes)
    }

    override fun showTransactionClosesSeconds(seconds: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_closes_in_seconds, seconds, seconds)
    }

    override fun showTransactionOpensDate(date: String) {
        transaction_date.text = getString(R.string.event_landing_opens, date)
    }

    override fun showTransactionOpensTomorrow() {
        transaction_date.text = getString(R.string.event_landing_opens_tomorrow)
    }

    override fun showTransactionOpensHours(hours: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_opens_in_hours, hours, hours)
    }

    override fun showTransactionOpensMinutes(minutes: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_opens_in_minutes, minutes, minutes)
    }

    override fun showTransactionOpensSeconds(seconds: Int) {
        transaction_date?.text = resources.getQuantityString(R.plurals.event_landing_opens_in_seconds, seconds, seconds)
    }

    override fun showTransactionLessThen24Hours(active: Boolean) {
        transaction_date.setTextColor(
            ContextCompat.getColor(context!!, if (active) R.color.color_input_error else R.color.color_white))
    }

    override fun showOnSaleMarketplaceActive(active: Boolean) {
        container_marketplace_info_on_sale.isVisible = active
    }

    override fun showListMarketplaceActive(active: Boolean) {
        container_marketplace_info_list.isVisible = active
    }

    override fun showMarketplaceChances(chances: Int, eventPassed: Boolean) {
        title_marketplace.text = when {
            eventPassed -> resources.getQuantityString(R.plurals.event_landing_passed_chances_caption, chances, chances)
            else -> resources.getQuantityString(R.plurals.event_landing_chances_caption, chances, chances)
        }
    }

    override fun showOnSaleMarketplaceLive(active: Boolean) {
        marketplace_info_on_sale_live.isVisible = active
    }

    override fun showListMarketplaceLive(active: Boolean) {
        marketplace_info_list_live.isVisible = active
    }

    override fun showOnSaleMarketplaceDate(startDate: String, endDate: String) {
        marketplace_info_on_sale_date.text = getString(R.string.event_landing_date_range, startDate, endDate)
    }

    override fun showListMarketplaceDate(startDate: String, endDate: String) {
        marketplace_info_list_date.text = getString(R.string.event_landing_date_range, startDate, endDate)
    }

    override fun showPerformerPictureUrl(url: String) {
        picture_performer.load(url)
    }

    override fun showUserPictureUrl(photo: String?) {
        picture_profile.load(photo)
    }

    override fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String) {
        navigator.navigateToShare.go(coverUri to getString(R.string.event_landing_share, name, location, deepLinkUrk))
    }

    override fun showAddEventToCalendar(calendarInfo: CalenderInfo) {
        navigator.navigateToCalendar.go(calendarInfo)
    }

    override fun showEventAddedToCalendar() {
        added_to_calendar.isVisible = true
    }

    override fun showTransactionDetails(id: String, type: String) {
        navigator.navigateToTransactionDetails.go(id to type)
    }

    override fun showTransactionHistory(items: List<TransactionOfferItem>) {
        adapter.submitList(items)
    }

    override fun showTransactionHistoryActive(active: Boolean) {
        transaction_list.isVisible = active
    }

    override fun showTransactionActionRequired() {
        transaction_date.setText(R.string.my_tickets_requested_action_required)
        transaction_date.setTextColor(ContextCompat.getColor(context!!, R.color.color_on_sale_chance_wont))
    }

    override fun showTransactionPendingResults() {
        transaction_date.setText(R.string.event_landing_transaction_pending)
        transaction_date.setTextColor(ContextCompat.getColor(context!!, R.color.color_white_alpha_70))
    }

    override fun showTransactionPendingAssignment() {
        transaction_date.setText(R.string.event_landing_transaction_pending_paid)
        transaction_date.setTextColor(ContextCompat.getColor(context!!, R.color.color_on_sale_chance_great))
    }

    override fun showMyRequest(multiple: Boolean) {
        transaction_my_request.setText(when {
            multiple -> R.string.event_landing_my_requests
            else ->R.string.event_landing_my_request
        })
    }

    override fun showMyRequestActionRequired(active: Boolean) {
        transaction_my_request.background = ContextCompat.getDrawable(context!!, when {
            active -> R.drawable.background_button_chances_negligible
            else -> R.drawable.background_button_accent
        })
    }

    override fun showMyOffersActive(active: Boolean) {
        transaction_my_offer.isVisible = active
    }

    override fun showMyOffersMultiple() {
        transaction_my_offer.setText(R.string.event_landing_transaction_my_offers)
    }

    override fun showMyOffers(amount: Int, currency: String) {
        transaction_my_offer.setPrice(amount, currency)
    }

    override fun showMyOffersChance(chance: Chance?) {
        transaction_my_offer.chance(chance)
    }

    override fun showGoingLoops() {
        val color = ContextCompat.getColor(context!!, R.color.color_white)
        picture_performer.changeColor(color)
        picture_profile.changeColor(color)
        loop_line.setBackgroundColor(color)
    }

    override fun showUserGoing(active: Boolean) {
        user_going.isVisible = active
    }

    override fun showTransactionEdit(id: String, type: String, offerGroupId: String) {
        navigator.navigateToTransactionEdit.go(Triple(id, type, offerGroupId))
    }

    override fun showGetMoreTickets(active: Boolean) {
        event_more_tickets.isVisible = active
    }

    override fun showGetMoreListTickets() {
        event_more_tickets_icon.setImageResource(R.drawable.ic_list_white_18dp)
    }

    override fun showGetMoreOnSaleTickets() {
        event_more_tickets_icon.setImageResource(R.drawable.ic_onsale_white_18dp)
    }

    override fun showAssignedTokensTitle(assigned: Int, total: Int) {
        title_marketplace.text = getString(R.string.event_landing_assigned_tickets, assigned, total)
    }

    override fun showTokenAssignments(active: Boolean) {
        event_assignments_list.isVisible = active
    }

    override fun showMarketplaceInfo(active: Boolean) {
        container_event_marketplace_info.isVisible = active
    }

    override fun showTokenAssignmentItems(items: List<TokenItem>) {
        assignmentAdapter.submitList(items)
    }

    override fun showTicketDetails(id: String, ticketId: String) {
        navigator.navigateToTicketDetails.go(id to ticketId)
    }

    override fun showTransactionSheetListener(active: Boolean) {
        val transactionsSheet = BottomSheetBehavior.from(transactions_sheet)
        if (active) {
            transactionsSheet.listener { state ->
                when {
                    state == BottomSheetBehavior.STATE_EXPANDED -> {
                        transaction_my_request.rotateUp()
                        transaction_my_offer.rotateUp()
                        overlay.isVisible = true
                        viewModel.transactionsExpanded(true)
                    }
                    state == BottomSheetBehavior.STATE_COLLAPSED -> {
                        transaction_my_request.rotateDown()
                        transaction_my_offer.rotateDown()
                        overlay.isVisible = false
                        viewModel.transactionsExpanded(false)
                    }
                }
            }
        } else {
            transactionsSheet.setBottomSheetCallback(null)
        }
    }

    @SuppressLint("MissingPermission")
    fun eventAddedToCalendar() {
        activity!!.contentResolver.query(CalendarContract.Events.CONTENT_URI, arrayOf(CalendarContract.Events.DESCRIPTION), { cursor ->
            if (cursor.moveToLast())
                viewModel.checkCalendarDeepLink(cursor.getString(0))
        })
    }

    override fun showTransactionFix(eventId: String, marketplaceType: String, offerGroupId: String) {
        navigator.navigateToTransactionFixPayment.go(Triple(eventId, marketplaceType, offerGroupId))
    }

    override fun showTransactionClaim(eventId: String, marketplaceType: String, offerGroupId: String) {
        navigator.navigateToTransactionClaimTickets.go(Triple(eventId, marketplaceType, offerGroupId))
    }

    companion object {
        private const val ARG_EVENT_ID = "ARG_EVENT_ID"
        private const val RC_CALENDAR_PERMISSION = 4786
        private const val ANIMATION_DURATION = 200L

        fun newInstance(id: String): EventLandingFragment {
            return EventLandingFragment().apply {
                arguments = Bundle(1).apply {
                    putString(ARG_EVENT_ID, id)
                }
            }
        }
    }
}