package com.enovlab.yoop.ui.auth.forgotpass

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class ForgotPasswordViewModel
@Inject constructor(private val repository: AuthRepository,
                    private val validation: Validation) : StateViewModel<ForgotPasswordView>() {

    private var email: String? = null

    init {
        validation.addValidators(Validator.EMAIL)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    fun emailInputChanged(email: String) {
        this.email = email
        val valid = validation.validate(Validator.EMAIL, email)
        view?.showEmailValid(valid)
    }

    internal fun nextStepClicked(state: State) {
        view?.showInputFieldsClearedFocus()
        view?.hideKeyboard()
        when (state) {
            State.ENABLED -> sendInstructions()
            else -> view?.showEmailError()
        }
    }

    private fun sendInstructions() {
        action {
            repository.forgotPassword(email!!)
        }
        preferences.resetEmail = email
    }
}