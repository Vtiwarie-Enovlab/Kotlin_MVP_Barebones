package com.enovlab.yoop.ui.auth.signup.step.name

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/5/2018.
 */
interface NameView : StateView {
    fun showFirstName(firstName: String)
    fun showLastName(lastName: String)
    fun showFirstNameValid(valid: Boolean)
    fun showLastNameValid(valid: Boolean)
    fun showFirstNameError()
    fun showLastNameError()
    fun showInputValid(valid: Boolean)
}