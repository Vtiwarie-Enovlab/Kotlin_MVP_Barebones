package com.enovlab.yoop.ui.auth.resetpass

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
abstract class ResetPasswordBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeResetPasswordFragment(): ResetPasswordFragment

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel::class)
    internal abstract fun bindResetPasswordViewModel(viewModel: ResetPasswordViewModel): ViewModel
}