package com.enovlab.yoop.ui.transaction.details

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.details.adapter.OfferGroupAdapter
import com.enovlab.yoop.ui.transaction.details.adapter.OfferGroupItem
import com.enovlab.yoop.ui.transaction.details.adapter.OfferGroupItemDecoration
import com.enovlab.yoop.utils.ext.displaySize
import com.enovlab.yoop.utils.ext.loadImage
import com.enovlab.yoop.utils.ext.loadImageNoCrop
import com.enovlab.yoop.utils.ext.loadSvgImage
import kotlinx.android.synthetic.main.fragment_transaction_details.*
import kotlinx.android.synthetic.main.layout_transaction_details_app_bar.*


/**
 * Created by mtosk on 3/5/2018.
 */
class TransactionDetailsFragment : TransactionFragment<TransactionDetailsView, TransactionDetailsViewModel>(),
    TransactionDetailsView {

    override val vmClass = TransactionDetailsViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    private val adapter by lazy { OfferGroupAdapter() }
    private lateinit var decoration: OfferGroupItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.type = MarketplaceType.valueOf(arguments?.getString(ARG_MARKETPLACE_TYPE)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        share.setOnClickListener { viewModel.shareEventClicked(context!!) }

        price_sort.setOnClickListener { viewModel.sortingClicked(adapter.getItems()) }

        adapter.listener = { viewModel.offerGroupClicked(it.id, it.hasUserActivity) }
        decoration = OfferGroupItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small))
        offer_group_list.adapter = adapter
        offer_group_list.addItemDecoration(decoration)
        (offer_group_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun showEventName(name: String?) {
        title.text = name
    }

    override fun showEventDateLocation(date: String?, location: String?) {
        event_location_date.text = "$date • $location"
    }

    override fun showSeatMap(url: String?, isSvg: Boolean) {
        when {
            isSvg -> transaction_seat_map.loadSvgImage(url)
            else -> transaction_seat_map.loadImageNoCrop(url)
        }
    }

    override fun showUserPictureUrl(photo: String?) {
        picture_profile.load(photo)
    }

    override fun showPerformerPictureUrl(url: String) {
        picture_performer.load(url)
    }

    override fun showPriceTypeMinimumAsk() {
        price_type.setText(R.string.transaction_details_price_min_ask)
    }

    override fun showPriceTypeList() {
        price_type.setText(R.string.transaction_details_price_list)
    }

    override fun showEventSharing(coverUri: Uri, name: String, location: String, deepLinkUrk: String) {
        navigator.navigateToShare.go(coverUri to getString(R.string.event_landing_share, name, location, deepLinkUrk))
    }

    override fun showSortingByMinAsk() {
        price_sort.setText(R.string.transaction_details_sort_min_ask)
    }

    override fun showSortingByAverageOffer() {
        price_sort.setText(R.string.transaction_details_sort_avg_offer)
    }

    override fun showSorting(active: Boolean) {
        price_sort.isVisible = active
    }

    override fun showOfferGroups(offerGroups: List<OfferGroupItem>) {
        adapter.submitList(offerGroups)
    }

    override fun showSeatMapHeight() {
        val displayHeight = context!!.displaySize().second
        val defaultSeatMapHeight = resources.getDimensionPixelSize(R.dimen.seat_map_default_height)
        val seatMapSpace = defaultSeatMapHeight + app_bar.height
        offer_group_list.doOnLayout {
            val listItemsHeight = decoration.itemsTotalHeight
            if (seatMapSpace + listItemsHeight < displayHeight) {
                val increase = (displayHeight - (seatMapSpace + listItemsHeight)) - it.paddingTop - it.paddingBottom
                if (container_transaction_seat_map.height < defaultSeatMapHeight + increase) {
                    container_transaction_seat_map.layoutParams = container_transaction_seat_map.layoutParams.apply {
                        height += increase
                    }
                }
            }
        }
    }

    override fun showCountSelection(id: String, type: String, offerGroupId: String) {
        navigator.offerGroupId = offerGroupId
        navigator.navigateToTransactionCount.go(Triple(id, type, offerGroupId))
    }

    override fun showEditTransaction(id: String, type: String, offerGroupId: String) {
        navigator.offerGroupId = offerGroupId
        navigator.navigateToTransactionEdit.go(Triple(id, type, offerGroupId))
    }

    companion object {
        fun newInstance(id: String, type: String): TransactionDetailsFragment {
            return TransactionDetailsFragment().apply {
                arguments = Bundle(2).apply {
                    putString(ARG_EVENT_ID, id)
                    putString(ARG_MARKETPLACE_TYPE, type)
                }
            }
        }
    }
}