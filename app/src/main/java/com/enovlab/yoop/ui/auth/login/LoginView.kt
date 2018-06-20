package com.enovlab.yoop.ui.auth.login

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
interface LoginView : StateView {
    fun showInputValid(valid: Boolean)
    fun showEmailValid(valid:Boolean)
    fun showEmailError()
    fun showPasswordError()
    fun showInputFieldsClearedFocus()
}