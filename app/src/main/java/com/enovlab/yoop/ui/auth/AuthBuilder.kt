package com.enovlab.yoop.ui.auth

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.auth.forgotpass.ForgotPasswordBuilder
import com.enovlab.yoop.ui.auth.login.LoginBuilder
import com.enovlab.yoop.ui.auth.resetpass.ResetPasswordBuilder
import com.enovlab.yoop.ui.auth.signup.SignupBuilder
import com.enovlab.yoop.ui.auth.verify.VerificationBuilder
import com.enovlab.yoop.ui.auth.verify.expired.VerificationExpiredBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

@Module
abstract class AuthBuilder {

    @ContributesAndroidInjector(modules = [
        LoginBuilder::class,
        SignupBuilder::class,
        ForgotPasswordBuilder::class,
        VerificationBuilder::class,
        VerificationExpiredBuilder::class,
        ResetPasswordBuilder::class
    ])
    internal abstract fun contributeAuthActivity(): AuthActivity

    @Binds
    @IntoMap
    @ViewModelKey(AuthNavigator::class)
    internal abstract fun bindAuthNavigator(navigator: AuthNavigator): ViewModel
}