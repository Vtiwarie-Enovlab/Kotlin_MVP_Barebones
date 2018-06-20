package com.enovlab.yoop.ui.auth.verify

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

interface VerificationView : StateView {
    fun showTitleSignUp()
    fun showTitleForgotPassword()
    fun showEmail(email: String)
}