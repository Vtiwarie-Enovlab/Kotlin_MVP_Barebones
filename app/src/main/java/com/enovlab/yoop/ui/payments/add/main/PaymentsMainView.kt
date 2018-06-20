package com.enovlab.yoop.ui.payments.add.main

import com.enovlab.yoop.ui.base.state.StateView

/**
 * @author vishaan
 */
interface PaymentsMainView : StateView {
    fun showCardDetails()
    fun showCardDetails(scanResult: PaymentsMainViewModel.ScanResult?)
    fun showBillingDetails()
    fun showNextStepEnabled(enabled: Boolean)
    fun showNextStepLoading(active: Boolean)
    fun showNextStepLoadingSuccess()
    fun showCardValidateInput()
    fun showBillingValidateInput()
    fun showAddPayment()
    fun showPaymentAdded()
    fun showClearFocus()
}