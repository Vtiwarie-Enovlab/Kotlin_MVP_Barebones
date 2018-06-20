package com.enovlab.yoop.ui.profile.intro

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class IntroBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeIntroFragment(): IntroFragment

    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    internal abstract fun bindIntroViewModel(viewModel: IntroViewModel): ViewModel

}