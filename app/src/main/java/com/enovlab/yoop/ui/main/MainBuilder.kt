package com.enovlab.yoop.ui.main

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.main.discover.DiscoverBuilder
import com.enovlab.yoop.ui.main.mytickets.MyTicketsBuilder
import com.enovlab.yoop.ui.main.profile.ProfileBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
abstract class MainBuilder {

    @ContributesAndroidInjector(modules = [
        DiscoverBuilder::class,
        MyTicketsBuilder::class,
        ProfileBuilder::class
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
