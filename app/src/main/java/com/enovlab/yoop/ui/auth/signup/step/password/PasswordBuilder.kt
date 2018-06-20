package com.enovlab.yoop.ui.auth.signup.step.password

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by mtosk on 3/5/2018.
 */

@Module
abstract class PasswordBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributePasswordFragment(): PasswordFragment

    @Binds
    @IntoMap
    @ViewModelKey(PasswordViewModel::class)
    internal abstract fun bindPasswordViewModel(viewModel: PasswordViewModel): ViewModel
}