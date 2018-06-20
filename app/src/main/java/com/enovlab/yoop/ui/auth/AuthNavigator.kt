package com.enovlab.yoop.ui.auth

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class AuthNavigator @Inject constructor() : ViewModel() {
    internal val navigateToSignup = PublishSubject.create<Unit>()
    internal val navigateToLogin = PublishSubject.create<Unit>()
    internal val navigateToVerifyEmailSignup = PublishSubject.create<Pair<String, Long>>()
    internal val navigateToVerifyEmailForgotPassword = PublishSubject.create<Pair<String, Long>>()
    internal val navigateBack = PublishSubject.create<Pair<Boolean, Long>>()
    internal val navigateToInbox = PublishSubject.create<Unit>()
    internal val navigateToForgotPassword = PublishSubject.create<Unit>()
    internal val navigateToWebUrl = PublishSubject.create<String>()

    enum class Navigation {
        LOGIN, SIGNUP, SIGNUP_VERIFICATION_EXPIRED, RESET_PASSWORD_VERIFICATION_EXPIRED, RESET_PASSWORD
    }
}