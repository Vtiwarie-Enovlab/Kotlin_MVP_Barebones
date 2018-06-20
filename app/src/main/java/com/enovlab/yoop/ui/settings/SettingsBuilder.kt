package com.enovlab.yoop.ui.settings

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.settings.about.AboutBuilder
import com.enovlab.yoop.ui.settings.notifications.NotificationsBuilder
import com.enovlab.yoop.ui.settings.support.SupportBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class SettingsBuilder {

    @ContributesAndroidInjector(modules = [
        NotificationsBuilder::class,
        SupportBuilder::class,
        AboutBuilder::class
    ])
    internal abstract fun contributeSettingsActivity(): SettingsActivity

    @Binds
    @IntoMap
    @ViewModelKey(SettingsNavigator::class)
    internal abstract fun bindSettingsNavigator(navigator: SettingsNavigator): ViewModel
}
