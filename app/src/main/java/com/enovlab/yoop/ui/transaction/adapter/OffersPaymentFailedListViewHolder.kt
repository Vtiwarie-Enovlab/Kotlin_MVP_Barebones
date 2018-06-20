package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.CustomTimer
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_failed.*

class OffersPaymentFailedListViewHolder(parent: ViewGroup, val listener: ((TransactionOfferItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_failed, parent)) {

    fun bind(item: TransactionOfferItem.PaymentFailedListOfferItem, timer: CustomTimer?) {
        val res = itemView.resources

        requested_failed_description.text = res.getString(R.string.event_landing_transaction_description,
            item.ticketCount, item.description)

        if (timer == null) {
            requested_failed_retry_time.setText(R.string.my_tickets_requested_payment_failed)
        } else {
            timer.tickListener = {
                when {
                    it.hours > 0 -> requested_failed_retry_time.text = res.getQuantityString(R.plurals.my_tickets_requested_payment_failed_hours, it.hours, it.hours)
                    it.minutes > 0 -> requested_failed_retry_time.text = res.getQuantityString(R.plurals.my_tickets_requested_payment_failed_minutes, it.minutes, it.minutes)
                    else -> requested_failed_retry_time.text = res.getQuantityString(R.plurals.my_tickets_requested_payment_failed_seconds, it.seconds, it.seconds)
                }
            }
            timer.finishListener = { bind(item, null) }
            timer.start()
        }

        requested_failed_fix.isVisible = timer != null

        if (listener != null)
            requested_failed_fix.setOnClickListener { listener.invoke(item) }
    }
}