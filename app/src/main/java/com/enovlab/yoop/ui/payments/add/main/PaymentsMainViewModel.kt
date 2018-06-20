package com.enovlab.yoop.ui.payments.add.main

import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import javax.inject.Inject

/**
 * @author vishaan
 */
class PaymentsMainViewModel
@Inject constructor(private val userRepository: UserRepository) : StateViewModel<PaymentsMainView>() {

    internal var step: Step = Step.CARD_DETAILS

    internal fun initialStep() {
        stepSelected()
    }

    override fun start() {
        when {
            userRepository.isAuthorized() -> load { userRepository.refreshUser().toCompletable() }
        }
    }

    fun inputValidation(valid: Boolean) {
        view?.showNextStepEnabled(valid)
    }

    fun loadingStarted(active: Boolean) {
        view?.showNextStepLoading(active)
    }

    fun success() {
        view?.showClearFocus()
        view?.showNextStepLoadingSuccess()
        view?.showPaymentAdded()
    }

    data class ScanResult(val cardNumber: String?, val expiryDate: String?)

    internal fun nextStepClicked(state: State?) {
        when (step) {
            Step.CARD_DETAILS -> when (state) {
                State.ENABLED -> nextStep()
                else -> view?.showCardValidateInput()
            }
            Step.BILLING_DETAILS -> when (state) {
                State.ENABLED -> {
                    view?.showAddPayment()
                }
                else -> view?.showBillingValidateInput()
            }
        }
    }

    private fun nextStep() {
        if (step.ordinal < Step.values().size - 1) {
            step = Step.values()[step.ordinal + 1]
            stepSelected()
        }
    }

    private fun stepSelected() {
        when (step) {
            Step.CARD_DETAILS -> view?.showCardDetails()
            Step.BILLING_DETAILS -> view?.showBillingDetails()
        }
    }

    enum class Step {
        CARD_DETAILS,
        BILLING_DETAILS
    }
}