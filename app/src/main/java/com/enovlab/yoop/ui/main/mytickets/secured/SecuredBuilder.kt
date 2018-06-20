package com.enovlab.yoop.ui.main.mytickets.secured

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class SecuredBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeSecuredFragment(): SecuredFragment

    @Binds
    @IntoMap
    @ViewModelKey(SecuredViewModel::class)
    internal abstract fun bindSecuredViewModel(viewModel: SecuredViewModel): ViewModel
}