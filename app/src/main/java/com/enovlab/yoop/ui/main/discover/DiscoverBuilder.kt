package com.enovlab.yoop.ui.main.discover

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by mtosk on 3/8/2018.
 */
@Module
abstract class DiscoverBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeDiscoverFragment(): DiscoverFragment

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    internal abstract fun bindDiscoverViewModel(viewModel: DiscoverViewModel): ViewModel
}