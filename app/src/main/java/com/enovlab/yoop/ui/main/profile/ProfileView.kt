package com.enovlab.yoop.ui.main.profile

import com.enovlab.yoop.ui.base.state.StateView

interface ProfileView : StateView {
    fun showUnauthorized(active: Boolean)
    fun showEmailVerifiedNoPhoto(active: Boolean)
    fun showEmailNotVerified(active: Boolean)
    fun showPendingVerification(active: Boolean)

    fun showPaymentMethods(active: Boolean)
    fun showPreferences(active: Boolean)
    fun showSteps(active: Boolean)
    fun showStepSignup(active: Boolean)
    fun showStepReady(active: Boolean)
    fun showStepVerified(active: Boolean)
    fun showEmailAddress(email: String)
    fun showUsername(username: String)
    fun showUserPhoto(url: String?)
    fun showVerifiedBackgroundEnabled(enabled: Boolean)
    fun showVerified(active: Boolean)
    fun showVerificationDate(verifyDate: String)

    fun showProfileCapture()
    fun showProfileIntro()
}