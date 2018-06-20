package com.enovlab.yoop.ui.auth.signup.step.name

import com.enovlab.yoop.data.Validation
import com.enovlab.yoop.data.Validator
import com.enovlab.yoop.ui.base.state.StateViewModel
import javax.inject.Inject

/**
 * Created by mtosk on 3/5/2018.
 */
class NameViewModel
@Inject constructor(private val validation: Validation) : StateViewModel<NameView>() {

    internal var firstName: String? = null
    internal var lastName: String? = null

    init {
        validation.addValidators(Validator.FIRST_NAME, Validator.LAST_NAME)
        validation.validInput.subscribe {
            view?.showInputValid(it)
        }
    }

    override fun start() {
        validation.check()
        if (firstName != null) view?.showFirstName(firstName!!)
        if (lastName != null) view?.showLastName(lastName!!)
    }

    internal fun onFirstNameInputChanged(firstName: String) {
        this.firstName = firstName
        val valid = validation.validate(Validator.FIRST_NAME, firstName)
        view?.showFirstNameValid(valid)
    }

    internal fun onLastNameInputChanged(lastName: String) {
        this.lastName = lastName
        val valid = validation.validate(Validator.LAST_NAME, lastName)
        view?.showLastNameValid(valid)
    }

    // should be called only when the input is not valid
    internal fun validateInput() {
        if (firstName == null || !validation.validate(Validator.FIRST_NAME, firstName!!)) {
            view?.showFirstNameError()
        }
        if (lastName == null || !validation.validate(Validator.LAST_NAME, lastName!!)) {
            view?.showLastNameError()
        }
    }
}