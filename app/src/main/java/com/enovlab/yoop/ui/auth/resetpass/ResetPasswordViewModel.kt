package com.enovlab.yoop.ui.auth.resetpass

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.ValidationUtil
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class ResetPasswordViewModel
@Inject constructor(private val repository: AuthRepository,
                    private val validation: Validation) : StateViewModel<ResetPasswordView>() {

    internal var token: String? = null
    private var password: String? = null

    init {
        validation.addValidators(Validator.PASSWORD)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    internal fun onPasswordInputChanged(password: String) {
        this.password = password
        val valid = validation.validate(Validator.PASSWORD, password)
        view?.showPasswordValid(valid)

        val has8CharactersValid = ValidationUtil.has8OrMoreChars(password)
        view?.showHas8CharactersValid(has8CharactersValid)

        val hasNumbers = ValidationUtil.hasNumber(password)
        view?.showHasNumbersValid(hasNumbers)
    }

    internal fun nextStepClicked(state: State) {
        view?.showInputFieldsClearedFocus()
        view?.hideKeyboard()
        if (state == State.ENABLED) resetPassword()

    }

    internal fun resetPassword() {
        action {
            repository.resetPassword(password!!, token!!).toCompletable()
        }
    }
}