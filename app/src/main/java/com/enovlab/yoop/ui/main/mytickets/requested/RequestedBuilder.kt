package com.enovlab.yoop.ui.main.mytickets.requested

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class RequestedBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeRequestedFragment(): RequestedFragment

    @Binds
    @IntoMap
    @ViewModelKey(RequestedViewModel::class)
    internal abstract fun bindRequestedViewModel(viewModel: RequestedViewModel): ViewModel
}