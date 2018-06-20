package com.enovlab.yoop.ui.auth.signup.step.email

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

/**
 * Created by mtosk on 3/5/2018.
 */
class EmailViewModel
@Inject constructor(private val repository: AuthRepository,
                    private val validation: Validation) : StateViewModel<EmailView>() {

    internal var email: String? = null

    init {
        validation.addValidators(Validator.EMAIL)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    override fun start() {
        validation.check()
        if (email != null) view?.showEmailAddress(email!!)
    }

    internal fun onEmailInputChanged(email: String) {
        this.email = email
        val valid = validation.validate(Validator.EMAIL, email)
        view?.showEmailValid(valid)
    }

    internal fun validateInput() {
        if (email == null || !validation.validate(Validator.EMAIL, email!!)) {
            view?.showEmailError()
        }
    }

    internal fun checkEmailAddress() {
        action {
            repository.isEmailAvailable(email!!)
        }
    }

    fun clearInputFieldsFocus() {
        view?.showInputFieldsClearedFocus()
    }
}