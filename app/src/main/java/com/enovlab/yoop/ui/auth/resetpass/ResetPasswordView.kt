package com.enovlab.yoop.ui.auth.resetpass

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

interface ResetPasswordView : StateView {
    fun showInputValid(valid: Boolean)
    fun showPasswordValid(valid: Boolean)
    fun showHas8CharactersValid(valid: Boolean)
    fun showHasNumbersValid(valid: Boolean)
    fun showInputFieldsClearedFocus()
}