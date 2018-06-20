package com.enovlab.yoop.ui.auth.signup.step.password

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.ValidationUtil
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.data.repository.AuthRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

/**
 * Created by mtosk on 3/5/2018.
 */
class PasswordViewModel
@Inject constructor(private val repository: AuthRepository,
                    private val validation: Validation) : StateViewModel<PasswordView>() {

    private var password: String? = null

    init {
        validation.addValidators(Validator.PASSWORD)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    override fun start() {
        validation.check(false)
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

    fun createAccount(firstName: String, lastName: String, email: String) {
        action {
            repository.signup(email, password!!, firstName, lastName).toCompletable()
        }
    }

    fun clearInputFieldsFocus() {
        view?.showInputFieldsClearedFocus()
    }
}