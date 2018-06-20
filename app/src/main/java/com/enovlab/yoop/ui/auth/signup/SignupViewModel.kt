package com.enovlab.yoop.ui.auth.signup

import com.enovlab.yoop.ui.auth.signup.step.Step
import com.enovlab.yoop.ui.auth.signup.step.Step.*
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class SignupViewModel
@Inject constructor() : StateViewModel<SignupView>() {

    internal var step: Step = NAME
        set(value) {
            field = value
            view?.showCurrentStep(step.step, STEP_SIZE)
        }

    internal fun initialStep() {
        stepSelected()
    }

    internal fun nextStepClicked(state: State) {
        view?.hideKeyboard()
        when (step) {
            Step.NAME -> when (state) {
                State.ENABLED -> nextStep()
                else -> view?.showNameValidateInput()
            }
            Step.EMAIL -> {
                view?.showEmailInputFieldsClearedFocus()
                when (state) {
                    State.ENABLED -> view?.showEmailAddressCheck()
                    else -> view?.showEmailValidateInput()
                }
            }
            Step.PASSWORD -> {
                view?.showPasswordInputFieldsClearedFocus()
                if (state == State.ENABLED) {
                    view?.showCreateAccount()
                }
            }
        }
    }

    internal fun backstackChanged(entries: Int) {
        view?.showBackButton(entries > 1)
        view?.removeSnackbar()
        view?.showNotes(step == Step.NAME)
    }

    fun inputValidation(valid: Boolean) {
        view?.showNextStepEnabled(valid)
    }

    fun createSnackbar(text: String, actionText: String? = null, action: (() -> Unit)? = null) {
        view?.createSnackbar(text, actionText, action)
    }

    fun removeSnackbar() {
        view?.removeSnackbar()
    }

    fun loadingStarted(active: Boolean) {
        view?.showNextStepLoading(active)
    }

    fun emailCheckedSuccess() {
        nextStep()
    }

    fun accountCreated() {
        view?.showNextStepLoadingSuccess()
        view?.showAccountCreated()
    }

    private fun nextStep() {
        if (step.ordinal < STEP_SIZE - 1) {
            step = values()[step.ordinal + 1]
            stepSelected()
        }
    }

    private fun stepSelected() {
        when (step) {
            NAME -> view?.showNameScreen()
            EMAIL -> view?.showEmailScreen()
            PASSWORD -> view?.showPasswordScreen()
        }
    }

    companion object {
        private val STEP_SIZE = Step.values().size
    }
}