package com.enovlab.yoop.ui.auth.login

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

class LoginViewModel
@Inject constructor(private val repository: AuthRepository,
                    private val validation: Validation) : StateViewModel<LoginView>() {

    internal var password: String? = null
    internal var email: String? = null

    init {
        validation.addValidators(Validator.EMAIL, Validator.PASSWORD_EMPTY)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    internal fun nextStepClicked(state: State?) {
        view?.showInputFieldsClearedFocus()
        view?.hideKeyboard()
        when (state) {
            State.ENABLED -> login(email!!, password!!)
            else -> {
                if (email == null || !validation.validate(Validator.EMAIL, email!!)) {
                    view?.showEmailError()
                }
                if (password == null || !validation.validate(Validator.PASSWORD_EMPTY, password!!)) {
                    view?.showPasswordError()
                }
            }
        }
    }

    internal fun emailInputChanged(email: String) {
        this.email = email
        val valid = validation.validate(Validator.EMAIL, email)
        view?.showEmailValid(valid)
    }

    internal fun passwordInputChanged(password: String) {
        this.password = password
        validation.validate(Validator.PASSWORD_EMPTY, password)
    }

    internal fun login(email: String, password: String) {
        action {
            repository.login(email, password).toCompletable()
        }
    }
}