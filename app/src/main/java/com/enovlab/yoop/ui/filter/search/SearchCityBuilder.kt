package com.enovlab.yoop.ui.filter.search

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
abstract class SearchCityBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeSearchFragment(): SearchCityFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchCityViewModel::class)
    internal abstract fun bindSearchViewModel(viewModel: SearchCityViewModel): ViewModel
}
