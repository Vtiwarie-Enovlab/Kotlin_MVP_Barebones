package com.enovlab.yoop.ui.auth.signup

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

interface SignupView : StateView {
    fun showBackButton(show: Boolean)
    fun showCurrentStep(step: Int, size: Int)
    fun showNextStepEnabled(enabled: Boolean)
    fun showNameScreen()
    fun showEmailScreen()
    fun showPasswordScreen()
    fun showNameValidateInput()
    fun showEmailValidateInput()
    fun showCreateAccount()
    fun createSnackbar(text: String, actionText: String?, action: (() -> Unit)?)
    fun removeSnackbar()
    fun showEmailAddressCheck()
    fun showNextStepLoading(active: Boolean)
    fun showNextStepLoadingSuccess()
    fun showAccountCreated()
    fun showNotes(show: Boolean)
    fun showEmailInputFieldsClearedFocus()
    fun showPasswordInputFieldsClearedFocus()
}