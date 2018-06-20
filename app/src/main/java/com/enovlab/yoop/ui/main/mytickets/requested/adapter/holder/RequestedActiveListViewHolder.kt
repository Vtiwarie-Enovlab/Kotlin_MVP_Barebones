package com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder

import android.support.v7.widget.SimpleItemAnimator
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedAdapter
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem.ActiveListItem
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersAdapter
import com.enovlab.yoop.ui.transaction.adapter.TransactionOffersItemDecoration
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_requested_active_list.*

class RequestedActiveListViewHolder(parent: ViewGroup,
                                    private val listener: ((RequestedItem) -> Unit)?,
                                    private val editListener: ((RequestedAdapter.EditData) -> Unit)? = null)
    : RequestedBaseViewHolder(inflateView(R.layout.item_requested_active_list, parent)) {

    fun bind(item: ActiveListItem) {
        val context = itemView.context

        requested_image.loadImage(item.mediaUrl)
        requested_title.text = item.name
        requested_location.text = "${item.date} • ${item.location}"

        requested_pill_date.text = context.getString(R.string.my_tickets_pill_close_date, item.marketplaceEndDate)

        val adapter = TransactionOffersAdapter()
        adapter.listenerEdit = {
            editListener?.invoke(RequestedAdapter.EditData(item.id, MarketplaceType.DRAW, it.id))
        }
        requested_active_offers.adapter = adapter
        if (requested_active_offers.itemDecorationCount == 0) {
            requested_active_offers.addItemDecoration(TransactionOffersItemDecoration(context.resources.getDimensionPixelSize(R.dimen.margin_small)))
        }
        (requested_active_offers.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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