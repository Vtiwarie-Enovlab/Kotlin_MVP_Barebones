package com.enovlab.yoop.ui.transaction.count

import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem
import com.enovlab.yoop.ui.transaction.count.adapter.CountPickerAdapter
import com.enovlab.yoop.ui.transaction.count.adapter.CountPickerItemDecoration
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import com.enovlab.yoop.utils.ext.fadeIn
import com.enovlab.yoop.utils.ext.fadeOut
import com.enovlab.yoop.utils.ext.loadImage
import com.enovlab.yoop.utils.ext.showKeyboard
import kotlinx.android.synthetic.main.fragment_transaction_count.*
import kotlinx.android.synthetic.main.layout_transaction_count_app_bar.*
import kotlinx.android.synthetic.main.layout_transaction_count_summary.*

class TransactionCountFragment : TransactionFragment<TransactionCountView, TransactionCountViewModel>(),
    TransactionCountView {

    override val vmClass = TransactionCountViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    private val adapter by lazy { CountPickerAdapter() }
    private val drawer by lazy { BottomSheetDialog(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.type = MarketplaceType.valueOf(arguments?.getString(ARG_MARKETPLACE_TYPE)!!)
        viewModel.offerGroupId = arguments?.getString(ARG_OFFER_GROUP_ID)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_count, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back.setOnClickListener { navigator.navigateBack.go(false to 100L) }
        share.setOnClickListener { viewModel.shareEventClicked(context!!) }

        adapter.listener = {
            drawer.hide()
            viewModel.countSelected(it, adapter.itemCount)
        }
        count_picker.adapter = adapter
        count_picker.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        count_picker.addItemDecoration(CountPickerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_large)))

        setupDrawer()

        chances_input.textDelayedChangeListener(viewModel::amountChanged)
        tickets_selected.setOnClickListener { viewModel.ticketsSelectedClicked(adapter.itemCount) }
        proceed_review.setOnClickListener { viewModel.proceed() }
    }

    private fun setupDrawer() {
        val sheetView = LayoutInflater.from(context!!).inflate(R.layout.layout_transaction_count_drawer, null)

        val picker = sheetView.findViewById<RecyclerView>(R.id.count_picker_drawer)
        picker.adapter = adapter
        picker.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        picker.addItemDecoration(CountPickerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_large)))

        drawer.setContentView(sheetView)
    }

    override fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String) {
        navigator.navigateToShare.go(coverUri to getString(R.string.event_landing_share, name, location, deepLinkUrk))
    }

    override fun showDescription(description: String?) {
        offer_group_description.text = description
    }

    override fun showDemandTitle(active: Boolean) {
        offer_group_demand.isVisible = active
    }

    override fun showHighDemand() {
        offer_group_demand.setText(R.string.transaction_count_demand_high)
        offer_group_demand.setTextColor(ContextCompat.getColor(context!!, R.color.color_on_sale_chance_poor))
    }

    override fun showExceedsSupplyDemand() {
        offer_group_demand.setText(R.string.transaction_count_demand_exceeds_supply)
        offer_group_demand.setTextColor(ContextCompat.getColor(context!!, R.color.color_on_sale_chance_wont))
    }

    override fun showSummaryPerformerPicture(url: String) {
        picture_performer.loadImage(url)
    }

    override fun showSummaryListPriceTitle() {
        transaction_price_title.setText(R.string.transaction_count_list_price)
    }

    override fun showSummaryPrice(currency: String?, price: Int) {
        transaction_price.text = "$currency$price"
    }

    override fun showSummaryListTicketsTitle() {
        transaction_offer_title.setText(R.string.transaction_count_list_tickets)
    }

    override fun showSummaryListTicketsIcon() {
        transaction_icon.setImageResource(R.drawable.ic_list_white_40dp)
    }

    override fun showSummaryListTicketsCount(count: Int) {
        transaction_offer.text = count.toString()
    }

    override fun showSummaryMinAskPriceTitle() {
        transaction_price_title.setText(R.string.transaction_count_min_ask)
    }

    override fun showSummaryOnSalePeopleIcon() {
        transaction_icon.setImageResource(R.drawable.ic_icon_people)
    }

    override fun showSummaryAverageOfferPriceTitle() {
        transaction_offer_title.setText(R.string.transaction_count_avg_offer)
    }

    override fun showSummaryAverageOfferPrice(currency: String?, price: Int) {
        transaction_offer.text = "$currency$price"
    }

    override fun showSummaryMinAskExceedsSupplyDemand() {
        val color = ContextCompat.getColor(context!!, R.color.color_white_alpha_50)
        transaction_price_title.setTextColor(color)
        transaction_price.setTextColor(color)
    }

    override fun showSummaryMinOffer(active: Boolean) {
        transaction_min_offer_title.isVisible = active
        transaction_min_offer.isVisible = active
    }

    override fun showSummaryMinOfferPrice(currency: String?, price: Int) {
        transaction_min_offer.text = "$currency$price"
    }

    override fun showPickerCount(items: List<CountItem>) {
        adapter.submitList(items)
    }

    override fun showTicketCountSelected(count: Int) {
        tickets_selected.text = resources.getQuantityString(R.plurals.transaction_count_selected_tickets, count, count)
    }

    override fun showProceedToReviewEnabled(enabled: Boolean) {
        proceed_review?.state = if (enabled) State.ENABLED else State.DISABLED_FULL
    }

    override fun showInputAmountCurrency(currency: String?) {
        chances_input.setCurrency(currency)
    }

    override fun showInputAmountHint(amountHint: String) {
//        chances_input.setHint(amountHint)
    }

    override fun showInputAmount(amount: String) {
        chances_input.setText(amount)
    }

    override fun showChancesDefault() {
        chances_input?.defaultState()
    }

    override fun showChances(chance: Chance) {
        chances_input?.chance(chance)
    }

    override fun showChancesWont(minAskPrice: Int) {
        chances_input?.wontChance(minAskPrice)
    }

    override fun showChancesNegligible() {
        chances_input?.negligibleChance()
    }

    override fun showTicketSelectedDrawer(items: List<CountItem>) {
        adapter.submitList(items)
        drawer.show()
    }

    override fun showTransactionClosesLessThen5Mins(active: Boolean) {
        transaction_less_five_min?.isVisible = active

        val margin = when {
            active -> resources.getDimensionPixelSize(R.dimen.margin_small)
            else -> resources.getDimensionPixelSize(R.dimen.margin_super_large)
        }
        back?.layoutParams = (back.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = margin
        }
        offer_group_description?.layoutParams  = (offer_group_description.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = margin
        }
        share?.layoutParams  = (share.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = margin
        }
    }

    override fun showTransactionClosesMinutes(minutes: Int) {
        transaction_less_five_min?.text = resources.getString(R.string.transaction_count_closes_in_minutes, minutes)
    }

    override fun showTransactionClosesSeconds(seconds: Int) {
        transaction_less_five_min?.text = resources.getString(R.string.transaction_count_closes_in_seconds, seconds)
    }

    override fun showTransactionClosed() {
        transaction_less_five_min?.setText(R.string.transaction_count_already_closed)
    }

    override fun showPicker(active: Boolean, duration: Long, animationListener: () -> Unit) = when {
        active -> {
            transaction_count_headline.fadeIn()
            count_picker.fadeIn()
        }
        else -> {
            transaction_count_headline.fadeOut(duration)
            count_picker.fadeOut(duration, animationListener)
        }
    }

    override fun showInput(active: Boolean, showKeyboard: Boolean, duration: Long) = when {
        active -> {
            if (active && showKeyboard) {
                chances_input.requestFocus()
                chances_input.showKeyboard()
            }
            transaction_count_headline_input.fadeIn(duration)
            chances_input.fadeIn(duration)
        }
        else -> {
            transaction_count_headline_input.fadeOut()
            chances_input.fadeOut()
        }
    }

    override fun showTicketSelected(active: Boolean) = when {
        active -> tickets_selected.fadeIn()
        else -> tickets_selected.fadeOut()
    }

    override fun showProceedToReview(active: Boolean) = when {
        active -> proceed_review.fadeIn()
        else -> proceed_review.fadeOut()
    }

    override fun showReview(id: String, type: String, offerGroupId: String, count: Int,
                            amount: Int?, chanceToken: String?, delay: Long) {
        navigator.navigateToTransactionReview.go(
            TransactionNavigator.ReviewParams(id,
                type = type, offerGroupId = offerGroupId,
                count = count, amount = amount, chanceToken = chanceToken,
                delay = delay)
        )
    }

    override fun showUserLimitActive(active: Boolean) {
        transaction_count_limits.isVisible = active
    }

    override fun showUserLimit(requested: Int, total: Int) {
        transaction_count_limits.text = resources.getString(R.string.transaction_count_tickets_limit, requested, total)
    }

    companion object {
        private const val ARG_OFFER_GROUP_ID = "ARG_OFFER_GROUP_ID"

        fun newInstance(id: String, type: String, offerGroupId: String): TransactionCountFragment {
            return TransactionCountFragment().apply {
                arguments = Bundle(3).apply {
                    putString(ARG_EVENT_ID, id)
                    putString(ARG_MARKETPLACE_TYPE, type)
                    putString(ARG_OFFER_GROUP_ID, offerGroupId)
                }
            }
        }
    }
}