package com.enovlab.yoop.ui.profile.details

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class ProfileDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeProfileDetailsFragment(): ProfileDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(ProfileDetailsViewModel::class)
    internal abstract fun bindProfileDetailsViewModel(viewModel: ProfileDetailsViewModel): ViewModel

}