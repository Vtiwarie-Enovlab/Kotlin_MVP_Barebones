package com.enovlab.yoop.ui.main.profile

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.main.MainFragment
import com.enovlab.yoop.ui.profile.ProfileEditActivity
import com.enovlab.yoop.utils.ext.applyToViews
import com.enovlab.yoop.utils.ext.loadImage
import com.enovlab.yoop.utils.ext.loadUserImage
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_profile_state_email_unverified.*
import kotlinx.android.synthetic.main.layout_profile_state_no_photo.*
import kotlinx.android.synthetic.main.layout_profile_state_pending.*
import kotlinx.android.synthetic.main.layout_profile_state_unauthorized.*
import kotlinx.android.synthetic.main.layout_profile_state_verified.*

class ProfileFragment : MainFragment<ProfileView, ProfileViewModel>(), ProfileView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = ProfileViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_login.setOnClickListener { navigator.navigateToAuthLogin.go() }
        profile_signup.setOnClickListener { navigator.navigateToAuthSignup.go() }
        profile_resend_link.setOnClickListener { viewModel.resendVerificationLink() }
        profile_get_ready.setOnClickListener { viewModel.createIdClicked() }

        View.OnClickListener {
            navigator.navigateToProfileDetails.go()
        }.applyToViews(profile_edit, profile_no_photo_edit, profile_edit_pending)

        profile_payments.setOnClickListener { navigator.navigateToManagePayments.go() }
        profile_preferences.setOnClickListener { navigator.navigateToNotifications.go() }
        profile_support.setOnClickListener { navigator.navigateToSupport.go() }
        profile_about_us.setOnClickListener { navigator.navigateToAbout.go() }
    }

    override fun showUnauthorized(active: Boolean) {
        profile_state_unauth.isVisible = active
    }

    override fun showEmailNotVerified(active: Boolean) {
        profile_state_email_unverified.isVisible = active
    }

    override fun showEmailVerifiedNoPhoto(active: Boolean) {
        profile_state_no_photo.isVisible = active
    }

    override fun showPendingVerification(active: Boolean) {
        profile_state_pending.isVisible = active
    }

    override fun showVerified(active: Boolean) {
        profile_state_verified.isVisible = active
    }

    override fun showPaymentMethods(active: Boolean) {
        profile_payments.isVisible = active
    }

    override fun showPreferences(active: Boolean) {
        profile_preferences.isVisible = active
    }

    override fun showSteps(active: Boolean) {
        profile_steps.isVisible = active
    }

    override fun showStepSignup(active: Boolean) {
        profile_steps.signUpActive(active)
    }

    override fun showStepReady(active: Boolean) {
        profile_steps.readyActive(active)
    }

    override fun showStepVerified(active: Boolean) {
        profile_steps.verifiedActive(active)
    }

    override fun showVerifiedBackgroundEnabled(enabled: Boolean) {
        container_profile.background = ContextCompat.getDrawable(context!!, when {
            enabled -> R.drawable.background_profile_verified
            else -> R.drawable.rect_rounded_accent_alpha_60
        })
    }

    override fun showEmailAddress(email: String) {
        profile_email_no_photo.text = email
        profile_email_unverified.text = email
        profile_email_pending.text = email
        profile_email_verified.text = email
    }

    override fun showUsername(username: String) {
        profile_username_no_photo.text = username
        profile_username_unverified.text = username
        profile_username_pending.text = username
        profile_username_verified.text = username
    }

    override fun showUserPhoto(url: String?) {
        profile_photo_pending.loadUserImage(url)
        profile_photo.loadUserImage(url)
    }

    override fun showVerificationDate(verifyDate: String) {
        profile_verified_date.text = getString(R.string.profile_verified_date, verifyDate)
    }

    override fun showProfileCapture() {
        navigator.navigateToProfileCapture.go()
    }

    override fun showProfileIntro() {
        navigator.navigateToProfileCaptureIntro.go()
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}