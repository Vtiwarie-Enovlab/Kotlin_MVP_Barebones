package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_event_landing_transaction_offer.*

/**
 * Created by mtosk on 3/8/2018.
 */
class OffersActiveNewViewHolder(parent: ViewGroup, val listener: (() -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_event_landing_transaction_offer, parent)) {

    fun bind(item: TransactionOfferItem) {
        when (item) {
            is TransactionOfferItem.ActiveListNewOfferItem -> {
                transaction_another_request.setText(R.string.event_landing_transaction_another_request)
            }
            is TransactionOfferItem.ActiveOnSaleNewOfferItem -> {
                transaction_another_request.setText(R.string.event_landing_transaction_another_offer)
            }
        }

        if (listener != null)
            containerView?.setOnClickListener { listener.invoke() }
    }
}