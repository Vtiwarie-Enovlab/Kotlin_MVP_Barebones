package com.enovlab.yoop.ui.search.event

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
abstract class SearchEventBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeSearchEventFragment(): SearchEventFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchEventViewModel::class)
    internal abstract fun bindSearchEventViewModel(viewModel: SearchEventViewModel): ViewModel
}
