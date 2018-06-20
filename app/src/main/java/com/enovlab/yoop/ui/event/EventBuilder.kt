package com.enovlab.yoop.ui.event

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.event.landing.EventLandingBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
abstract class EventBuilder {

    @ContributesAndroidInjector(modules = [
        EventLandingBuilder::class
    ])
    internal abstract fun contributeEventActivity(): EventActivity

    @Binds
    @IntoMap
    @ViewModelKey(EventNavigator::class)
    internal abstract fun bindEventNavigator(navigator: EventNavigator): ViewModel
}
