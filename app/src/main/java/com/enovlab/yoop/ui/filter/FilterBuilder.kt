package com.enovlab.yoop.ui.filter

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.filter.details.FilterDetailsBuilder
import com.enovlab.yoop.ui.filter.search.SearchCityBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by mtosk on 3/14/2018.
 */

@Module
abstract class FilterBuilder {

    @ContributesAndroidInjector(modules = [
        FilterDetailsBuilder::class,
        SearchCityBuilder::class
    ])
    internal abstract fun contributeFilterActivity(): FilterActivity

    @Binds
    @IntoMap
    @ViewModelKey(FilterNavigator::class)
    internal abstract fun bindFilterNavigator(navigator:FilterNavigator): ViewModel
}