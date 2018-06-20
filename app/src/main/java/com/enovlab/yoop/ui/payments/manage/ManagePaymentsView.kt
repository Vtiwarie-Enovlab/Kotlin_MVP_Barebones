package com.enovlab.yoop.ui.payments.manage

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.payments.manage.adapter.PaymentItem

/**
 * @author vishaan
 */
interface ManagePaymentsView : StateView {
    fun submitList(paymentItems: List<PaymentItem>)
}