package com.enovlab.yoop.ui.transaction.adapter

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.Chance
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_requested_transaction_active_on_sale.*

class OffersActiveOnSaleViewHolder(parent: ViewGroup, val listener: ((TransactionOfferItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_requested_transaction_active_on_sale, parent)) {

    fun bind(item: TransactionOfferItem.ActiveOnSaleOfferItem) {
        requested_on_sale_description.text = itemView.resources.getString(R.string.event_landing_transaction_description, item.ticketCount, item.description)
        requested_on_sale_price.text = "${item.currency}${item.amount}"

        requested_on_sale_chances.text = when (item.chance) {
            Chance.GREAT -> itemView.resources.getString(R.string.event_landing_transaction_chance_great)
            Chance.GOOD -> itemView.resources.getString(R.string.event_landing_transaction_chance_good)
            Chance.LOW -> itemView.resources.getString(R.string.event_landing_transaction_chance_ok)
            Chance.POOR -> itemView.resources.getString(R.string.event_landing_transaction_chance_poor)
            else -> itemView.resources.getString(R.string.transaction_count_chances_negligible)
        }

        val color = when (item.chance) {
            Chance.GREAT -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_great)
            Chance.GOOD -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_good)
            Chance.LOW -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_ok)
            Chance.POOR -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_poor)
            else -> ContextCompat.getColor(itemView.context, R.color.color_on_sale_chance_wont)
        }

        requested_on_sale_chances.setTextColor(color)
        requested_on_sale_price.setTextColor(color)
        requested_on_sale_price_each.setTextColor(color)

        if (listener != null)
            requested_on_sale_edit.setOnClickListener { listener.invoke(item) }
    }
}