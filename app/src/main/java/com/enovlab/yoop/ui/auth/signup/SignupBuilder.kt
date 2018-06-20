package com.enovlab.yoop.ui.auth.signup

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.auth.signup.step.email.EmailBuilder
import com.enovlab.yoop.ui.auth.signup.step.name.NameBuilder
import com.enovlab.yoop.ui.auth.signup.step.password.PasswordBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

@Module
abstract class SignupBuilder {

    @ContributesAndroidInjector(modules = [
        NameBuilder::class,
        EmailBuilder::class,
        PasswordBuilder::class
    ])
    internal abstract fun contributeSignupFragment(): SignupFragment

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    internal abstract fun bindSignupViewModel(viewModel: SignupViewModel): ViewModel
}