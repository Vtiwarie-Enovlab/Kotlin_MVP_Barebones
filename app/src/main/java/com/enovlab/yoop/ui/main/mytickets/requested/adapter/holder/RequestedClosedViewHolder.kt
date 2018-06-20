package com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem.ClosedItem
import com.enovlab.yoop.utils.ext.greyscale
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_requested_closed.*

class RequestedClosedViewHolder(parent: ViewGroup,
                                private val listener: ((RequestedItem) -> Unit)?,
                                private val archiveListener: ((List<String>) -> Unit)? = null)
    : RequestedBaseViewHolder(inflateView(R.layout.item_requested_closed, parent)) {

    fun bind(item: ClosedItem) {
        requested_image.loadImage(item.mediaUrl)
        requested_image.greyscale(true)
        requested_title.text = item.name
        requested_location.text = "${item.date} • ${item.location}"

        bindLostOffers(item.lostOffers)

        itemView.setOnClickListener { listener?.invoke(item) }

        requested_archive.setOnClickListener {
            archiveListener?.invoke(item.lostOffers.map { it.id })
        }
    }
}