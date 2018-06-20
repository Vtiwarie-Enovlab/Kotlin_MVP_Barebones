package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_pending.*

class OffersPendingOnSaleViewHolder(parent: ViewGroup)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_pending, parent)) {

    fun bind(item: TransactionOfferItem.PendingOnSaleOfferItem) {
        requested_pending_description.text = itemView.resources.getString(R.string.my_tickets_requested_on_sale_pending,
            item.ticketCount, item.currency, item.amount, item.description)
    }
}