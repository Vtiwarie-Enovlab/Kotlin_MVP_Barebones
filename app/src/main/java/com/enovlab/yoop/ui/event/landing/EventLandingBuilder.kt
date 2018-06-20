package com.enovlab.yoop.ui.event.landing

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
abstract class EventLandingBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeEventLandingFragment(): EventLandingFragment

    @Binds
    @IntoMap
    @ViewModelKey(EventLandingViewModel::class)
    internal abstract fun bindEventLandingViewModel(viewModel: EventLandingViewModel): ViewModel

}
