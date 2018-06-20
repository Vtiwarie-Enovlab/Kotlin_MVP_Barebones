package com.enovlab.yoop.ui.auth.forgotpass

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

@Module
abstract class ForgotPasswordBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    internal abstract fun bindForgotPasswordViewModel(viewModel: ForgotPasswordViewModel): ViewModel
}