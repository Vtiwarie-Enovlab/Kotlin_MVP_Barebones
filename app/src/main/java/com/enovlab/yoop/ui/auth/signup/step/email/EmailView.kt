package com.enovlab.yoop.ui.auth.signup.step.email

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/5/2018.
 */
interface EmailView : StateView {
    fun showEmailAddress(emailAddress: String)
    fun showInputValid(valid: Boolean)
    fun showEmailValid(valid: Boolean)
    fun showEmailError()
    fun showInputFieldsClearedFocus()
}