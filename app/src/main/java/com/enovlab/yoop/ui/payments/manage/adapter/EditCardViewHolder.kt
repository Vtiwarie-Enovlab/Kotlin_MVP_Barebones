package com.enovlab.yoop.ui.payments.manage.adapter

import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_manage_payments_edit_card.*

class EditCardViewHolder(parent: ViewGroup, val listener: ((String) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_manage_payments_edit_card, parent)) {

    fun bind(paymentItem: PaymentItem) {
        val payment = paymentItem.methodItem as PaymentItem.PaymentMethodItem.PaymentMethod

        container_manage_payment.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_manage_payments_white)
        when (payment.cardType) {
            CardType.MC -> card_logo.setImageResource(R.drawable.ic_mastercard_large)
            CardType.VI -> card_logo.setImageResource(R.drawable.ic_visa_large)
        }
        card_number.text = "**** " + payment.lastFour + " | "

        card_expiry.text = payment.expiryDate

        if (payment.isDefault ?: false) {
            default_card.isVisible = true
        }
        if (listener != null) {
            itemView.setOnClickListener { listener?.invoke(payment.id) }
        }
    }
}