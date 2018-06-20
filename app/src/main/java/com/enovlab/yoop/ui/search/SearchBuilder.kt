package com.enovlab.yoop.ui.search

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.search.event.SearchEventBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
abstract class SearchBuilder {

    @ContributesAndroidInjector(modules = [
        SearchEventBuilder::class
    ])
    internal abstract fun contributeSearchActivity(): SearchActivity

    @Binds
    @IntoMap
    @ViewModelKey(SearchNavigator::class)
    internal abstract fun bindSearchNavigator(navigator: SearchNavigator): ViewModel
}
