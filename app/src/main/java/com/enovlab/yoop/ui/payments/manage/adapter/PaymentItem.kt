package com.enovlab.yoop.ui.payments.manage.adapter

import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.data.entity.event.Event

/**
 * Created by vishaantiwarie on 3/16/18.
 */
data class PaymentItem(val methodItem: PaymentMethodItem) {

    sealed class PaymentMethodItem {
        data class PaymentMethod(val id: String, val cardType: CardType?, val lastFour: String?, val isDefault: Boolean?, val expiryDate: String?) : PaymentMethodItem()
        object AddNewPaymentMethod : PaymentMethodItem()
    }
}