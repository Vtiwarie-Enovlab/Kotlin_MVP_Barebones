package com.enovlab.yoop.ui.main

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainBuilder {

    @ContributesAndroidInjector(modules = [
    ])
    internal abstract fun contributeMainActivity(): MainActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainNavigator::class)
    internal abstract fun bindMainNavigator(navigator: MainNavigator): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}
