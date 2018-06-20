package com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedAdapter
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem.TryOnSaleItem
import com.enovlab.yoop.utils.ext.greyscale
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_requested_try_on_sale.*

class RequestedTryOnSaleViewHolder(parent: ViewGroup,
                                   private val listener: ((RequestedItem) -> Unit)?,
                                   private val detailsListener: ((RequestedAdapter.DetailsData) -> Unit)? = null,
                                   private val archiveListener: ((List<String>) -> Unit)? = null)
    : RequestedBaseViewHolder(inflateView(R.layout.item_requested_try_on_sale, parent)) {

    fun bind(item: TryOnSaleItem) {
        requested_image.loadImage(item.mediaUrl)
        requested_image.greyscale(true)
        requested_title.text = item.name
        requested_location.text = "${item.date} • ${item.location}"

        requested_pill_date.text = itemView.resources.getString(R.string.my_tickets_pill_close_date, item.marketplaceEndDate)

        bindLostOffers(item.lostOffers)

        itemView.setOnClickListener { listener?.invoke(item) }

        requested_try_next.setOnClickListener {
            detailsListener?.invoke(RequestedAdapter.DetailsData(item.id, MarketplaceType.AUCTION))
        }

        requested_archive.setOnClickListener {
            archiveListener?.invoke(item.lostOffers.map { it.id })
        }
    }
}