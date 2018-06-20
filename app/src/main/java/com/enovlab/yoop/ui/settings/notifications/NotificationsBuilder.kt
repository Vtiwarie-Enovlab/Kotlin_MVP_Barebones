package com.enovlab.yoop.ui.settings.notifications

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class NotificationsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeNotificationsFragment(): NotificationsFragment

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    internal abstract fun bindNotificationsViewModel(viewModel: NotificationsViewModel): ViewModel

}