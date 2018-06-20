package com.enovlab.yoop.ui.profile.details

import com.enovlab.yoop.ui.base.state.StateView

interface ProfileDetailsView : StateView {
    fun showPhoto(url: String?)
    fun showVerified(active: Boolean)
    fun showVerifiedCaption(verified: Boolean)
    fun showVerifiedTitle(verified: Boolean)
    fun showVerifiedPhoto(verified: Boolean)
    fun showFirstName(firstName: String)
    fun showLastName(lastName: String)
    fun showEmailAddress(email: String)
    fun showSaveChanges(active: Boolean)
    fun showPasswordChanged(active: Boolean)
    fun showPasswordChange(active: Boolean)
    fun showPasswordChangeReset()
    fun showEdittingFinished()
    fun showCapture()
    fun showIntro()
}