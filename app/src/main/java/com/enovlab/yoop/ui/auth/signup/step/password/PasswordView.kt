package com.enovlab.yoop.ui.auth.signup.step.password

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/5/2018.
 */
interface PasswordView : StateView {
    fun showInputValid(valid: Boolean)
    fun showPasswordValid(valid: Boolean)
    fun showHas8CharactersValid(valid: Boolean)
    fun showHasNumbersValid(valid: Boolean)
    fun showInputFieldsClearedFocus()
}