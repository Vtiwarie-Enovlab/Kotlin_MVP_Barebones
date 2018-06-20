package com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder

import android.support.v7.widget.SimpleItemAnimator
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedAdapter
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersAdapter
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersItemDecoration
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_requested_fix_list.*

class RequestedFixListViewHolder(parent: ViewGroup,
                                 private val listener: ((RequestedItem) -> Unit)?,
                                 private val fixListener: ((RequestedAdapter.EditData) -> Unit)?,
                                 private val claimListener: ((RequestedAdapter.EditData) -> Unit)?)
    : RequestedBaseViewHolder(inflateView(R.layout.item_requested_fix_list, parent)) {

    fun bind(item: RequestedItem.ActionRequiredListItem) {
        val context = itemView.context

        requested_image.loadImage(item.mediaUrl)
        requested_title.text = item.name
        requested_location.text = "${item.date} • ${item.location}"

        val adapter = TransactionOffersAdapter()
        adapter.listenerFix = {
            fixListener?.invoke(RequestedAdapter.EditData(item.id, MarketplaceType.DRAW, it.id))
        }
        adapter.listenerClaim = {
            claimListener?.invoke(RequestedAdapter.EditData(item.id, MarketplaceType.DRAW, it.id))
        }
        requested_fix_offers.adapter = adapter
        if (requested_fix_offers.itemDecorationCount == 0) {
            requested_fix_offers.addItemDecoration(TransactionOffersItemDecoration(context.resources.getDimensionPixelSize(R.dimen.margin_small)))
        }
        (requested_fix_offers.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.submitList(item.offers)

        if (item.lostOffers.isNotEmpty()) {
            requested_lost_offers.isVisible = true
            bindLostOffers(item.lostOffers)
        } else {
            requested_lost_offers.isVisible = false
        }

        itemView.setOnClickListener { listener?.invoke(item) }
    }
}