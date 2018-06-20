package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_paid_pending.*

class OffersPaidPendingListViewHolder(parent: ViewGroup)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_paid_pending, parent)) {

    fun bind(item: TransactionOfferItem.PaidPendingListOfferItem) {
        requested_paid_pending_description.text = itemView.resources.getString(R.string.event_landing_transaction_description,
            item.ticketCount, item.description)
    }
}