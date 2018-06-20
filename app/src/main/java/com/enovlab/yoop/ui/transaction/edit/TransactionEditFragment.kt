package com.enovlab.yoop.ui.transaction.edit

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.ui.transaction.count.adapter.CountItem
import com.enovlab.yoop.ui.transaction.count.adapter.CountPickerAdapter
import com.enovlab.yoop.ui.transaction.count.adapter.CountPickerItemDecoration
import com.enovlab.yoop.utils.ext.keyboardAutoListener
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.fragment_transaction_edit.*
import kotlinx.android.synthetic.main.layout_transaction_count_summary.*
import kotlinx.android.synthetic.main.layout_transaction_edit_app_bar.*
import kotlinx.android.synthetic.main.layout_transaction_edit_confirm.*
import kotlinx.android.synthetic.main.layout_transaction_edit_discard.*
import kotlinx.android.synthetic.main.layout_transaction_edit_more.*

class TransactionEditFragment : TransactionFragment<TransactionEditView, TransactionEditViewModel>(),
    TransactionEditView {

    override val vmClass = TransactionEditViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    private val adapter by lazy { CountPickerAdapter() }
    private val drawer by lazy { BottomSheetDialog(context!!) }
    private val confirmDialog by lazy { BottomSheetDialog(context!!) }
    private val discardDialog by lazy { BottomSheetDialog(context!!) }
    private val moreDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.type = MarketplaceType.valueOf(arguments?.getString(ARG_MARKETPLACE_TYPE)!!)
        viewModel.offerGroupId = arguments?.getString(ARG_OFFER_GROUP_ID)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close.setOnClickListener { viewModel.closeClicked() }
        more.setOnClickListener { viewModel.moreClicked() }

        adapter.listener = {
            drawer.hide()
            viewModel.countSelected(it, adapter.itemCount)
        }
        count_picker.adapter = adapter
        count_picker.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        count_picker.addItemDecoration(CountPickerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_large)))

        setupDrawer()

        chances_input.textDelayedChangeListener(viewModel::amountChanged)
        tickets_selected.setOnClickListener { viewModel.ticketsSelectedClicked() }
        delete_offer.setOnClickListener { viewModel.deleteClicked() }
        save_changes.setOnClickListener { viewModel.saveChangesClicked() }

        activity!!.keyboardAutoListener { isOpen ->
            if (getView() != null) {
                delete_offer?.isVisible = !isOpen
                moveSaveChangesTop(isOpen)
                moveSelectedTicketsLeft(isOpen)
            }
        }

        chances_input.focusListener = { hasFocus ->
            if (hasFocus) {
                showHeadlineChanged(true)
                showMyOfferHeadline(true)
            }
        }

        confirmDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_transaction_edit_confirm, null))
        confirmDialog.delete_confirm.setOnClickListener {
            viewModel.deleteConfirmClicked()
            confirmDialog.container_delete_confirm.isInvisible = true
        }
        confirmDialog.delete_cancel.setOnClickListener {
            confirmDialog.hide()
        }

        discardDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_transaction_edit_discard, null))
        discardDialog.discard_confirm.setOnClickListener {
            discardDialog.hide()
            showEditingFinished(false)
        }
        discardDialog.discard_cancel.setOnClickListener {
            discardDialog.hide()
        }

        moreDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_transaction_edit_more, null))
        moreDialog.more_order_summary.setOnClickListener {
            moreDialog.hide()
            viewModel.seeOrderSummaryClicked()
        }
        moreDialog.more_cancel.setOnClickListener {
            moreDialog.hide()
        }
    }

    private fun setupDrawer() {
        val sheetView = LayoutInflater.from(context!!).inflate(R.layout.layout_transaction_count_drawer, null)

        val picker = sheetView.findViewById<RecyclerView>(R.id.count_picker_drawer)
        picker.adapter = adapter
        picker.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        picker.addItemDecoration(CountPickerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_large)))

        drawer.setContentView(sheetView)
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

    override fun showInputAmountCurrency(currency: String?) {
        chances_input.setCurrency(currency)
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

    override fun showTicketSelectedDrawer() {
        drawer.show()
    }

    override fun showTransactionClosesLessThen5Mins(active: Boolean) {
        transaction_less_five_min?.isVisible = active

        val margin = when {
            active -> resources.getDimensionPixelSize(R.dimen.margin_small)
            else -> resources.getDimensionPixelSize(R.dimen.margin_super_large)
        }
        close?.layoutParams = (close.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = margin
        }
        offer_group_description?.layoutParams  = (offer_group_description.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = margin
        }
        more?.layoutParams  = (more.layoutParams as ConstraintLayout.LayoutParams).apply {
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

    override fun showHeadlineChanged(changed: Boolean) {
        transaction_count_headline.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (changed) 30f else 14f)
        transaction_count_headline.setAllCaps(!changed)
    }

    override fun showMyRequestHeadline(active: Boolean) {
        transaction_count_headline.updateLayoutParams<ConstraintLayout.LayoutParams> {
            verticalBias = 0.35f
        }
        transaction_count_headline.setText(when {
            active -> R.string.transaction_count_headline
            else -> R.string.event_landing_my_request
        })
    }

    override fun showPicker(active: Boolean) {
        count_picker.isVisible = active
    }

    override fun showMyOfferHeadline(active: Boolean) {
        transaction_count_headline.updateLayoutParams<ConstraintLayout.LayoutParams> {
            verticalBias = 0.10f
        }
        transaction_count_headline.setText(when {
            active -> R.string.transaction_count_headline_input
            else -> R.string.event_landing_transaction_my_offer
        })
    }

    override fun showInput(active: Boolean) {
        chances_input.isVisible = active
    }

    override fun showTicketSelected(active: Boolean) {
        tickets_selected.isVisible = active
    }

    override fun showSaveChanges(active: Boolean) {
        save_changes?.isVisible = active
        delete_offer?.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (active) 0f else 0.5f
        }
    }

    override fun showDeleteConfirmation() {
        confirmDialog.show()
    }

    override fun showActionIndicator(active: Boolean) {
        confirmDialog.delete_progress.isVisible = active
    }

    override fun showEditingFinished(finish: Boolean) {
        navigator.navigateBack.go(finish to 200L)
    }

    override fun showDiscardChangesConfirmation() {
        discardDialog.show()
    }

    override fun showMoreDialog() {
        moreDialog.show()
    }

    override fun showMoreOnSaleMarketplace() {
        moreDialog.more_maketplace_type.setText(R.string.transaction_edit_more_on_sale_offer)
    }

    override fun showMoreListMarketplace() {
        moreDialog.more_maketplace_type.setText(R.string.transaction_edit_more_list_request)
    }

    override fun showMoreEventName(name: String?) {
        moreDialog.more_event_name.text = name
    }

    override fun showReview(id: String, type: String, offerGroupId: String, count: Int,
                            amount: Int?, chanceToken: String?, delay: Long,
                            isUpdate: Boolean, isOverview: Boolean) {
        navigator.navigateToTransactionReview.go(TransactionNavigator.ReviewParams(id,
            type = type, offerGroupId = offerGroupId,
            count = count, amount = amount, chanceToken = chanceToken,
            isUpdate = isUpdate, isOverview = isOverview,
            delay = delay)
        )
    }

    override fun showError(message: String?) {
        super.showError(message)
        confirmDialog.hide()
    }

    override fun showErrorNoConnection() {
        super.showErrorNoConnection()
        confirmDialog.hide()
    }

    private fun moveSelectedTicketsLeft(left: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(container_transaction_count)
        constraintSet.setHorizontalBias(R.id.tickets_selected, if (left) 0f else 0.5f)
        constraintSet.applyTo(container_transaction_count)
    }

    private fun moveSaveChangesTop(top: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(container_transaction_count)
        constraintSet.setVerticalBias(R.id.save_changes, if (top) 0f else 1f)
        constraintSet.applyTo(container_transaction_count)
    }

    companion object {
        private const val ARG_OFFER_GROUP_ID = "ARG_OFFER_GROUP_ID"

        fun newInstance(id: String, type: String, offerGroupId: String): TransactionEditFragment {
            return TransactionEditFragment().apply {
                arguments = Bundle(3).apply {
                    putString(ARG_EVENT_ID, id)
                    putString(ARG_MARKETPLACE_TYPE, type)
                    putString(ARG_OFFER_GROUP_ID, offerGroupId)
                }
            }
        }
    }
}