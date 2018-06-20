package com.enovlab.yoop.ui.filter.details

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by mtosk on 3/14/2018.
 */

@Module
abstract class FilterDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeFilterDetailsFragment(): FilterDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(FilterDetailsViewModel::class)
    internal abstract fun bindFilterDetailsViewModel(viewModel: FilterDetailsViewModel): ViewModel
}