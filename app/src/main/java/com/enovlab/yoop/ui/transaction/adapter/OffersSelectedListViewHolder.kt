package com.enovlab.yoop.ui.transaction.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.CustomTimer
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_selected.*

class OffersSelectedListViewHolder(parent: ViewGroup, val listener: ((TransactionOfferItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_selected, parent)) {

    fun bind(item: TransactionOfferItem.SelectedListOfferItem, timer: CustomTimer?) {
        val res = itemView.resources

        requested_selected_description.text = res.getString(R.string.event_landing_transaction_description,
            item.ticketCount, item.description)

        if (timer == null) {
            requested_selected_claim_time.setText(R.string.my_tickets_requested_claim_failed)
        } else {
            timer.tickListener = {
                when {
                    it.hours > 0 -> requested_selected_claim_time.text = res.getQuantityString(R.plurals.my_tickets_requested_claim_hours, it.hours, it.hours)
                    it.minutes > 0 -> requested_selected_claim_time.text = res.getQuantityString(R.plurals.my_tickets_requested_claim_minutes, it.minutes, it.minutes)
                    else -> requested_selected_claim_time.text = res.getQuantityString(R.plurals.my_tickets_requested_claim_seconds, it.seconds, it.seconds)
                }
            }
            timer.finishListener = { bind(item, null) }
            timer.start()
        }

        requested_selected_claim.isVisible = timer != null

        if (listener != null)
            requested_selected_claim.setOnClickListener { listener.invoke(item) }
    }
}