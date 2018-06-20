package com.enovlab.yoop.ui.transaction.review.adapter

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_transaction_review_payments.*

class PaymentsViewHolder(parent: ViewGroup, val listener: ((PaymentMethod) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_transaction_review_payments, parent)) {

    fun bind(payment: PaymentMethod) {
        payments_card_number.text = itemView.resources.getString(R.string.transaction_review_card_numbers, payment.lastDigits)
        when (payment.cardType) {
            CardType.MC -> payments_card_icon.setImageResource(R.drawable.icon_payment_card_mc)
            CardType.VI -> payments_card_icon.setImageResource(R.drawable.icon_payment_card_visa)
        }

        val isDefault = payment.isDefault == true

        payments_card_selected.isVisible = isDefault
        payments_card_number.setTextColor(ContextCompat.getColor(itemView.context, when {
            isDefault -> R.color.colorPrimary
            else -> R.color.color_white
        }))
        itemView.isActivated = isDefault

        itemView.setOnClickListener { listener?.invoke(payment) }
    }
}