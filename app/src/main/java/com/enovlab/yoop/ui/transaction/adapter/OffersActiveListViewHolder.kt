package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_active_list.*

class OffersActiveListViewHolder(parent: ViewGroup, val listener: ((TransactionOfferItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_active_list, parent)) {

    fun bind(item: TransactionOfferItem.ActiveListOfferItem) {
        requested_list_description.text = itemView.resources.getString(R.string.event_landing_transaction_description, item.ticketCount, item.description)
        requested_list_price.text = "${item.currency}${item.listPrice}"
        requested_list_edit.isVisible = true

        if (listener != null)
            requested_list_edit.setOnClickListener { listener.invoke(item) }
    }
}