package com.enovlab.yoop.ui.auth.signup.step.name

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
abstract class NameBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeNameFragment(): NameFragment

    @Binds
    @IntoMap
    @ViewModelKey(NameViewModel::class)
    internal abstract fun bindNameViewModel(viewModel: NameViewModel): ViewModel
}