package com.enovlab.yoop.ui.settings.support

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class SupportBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeSupportFragment(): SupportFragment

    @Binds
    @IntoMap
    @ViewModelKey(SupportViewModel::class)
    internal abstract fun bindSupportViewModel(viewModel: SupportViewModel): ViewModel

}