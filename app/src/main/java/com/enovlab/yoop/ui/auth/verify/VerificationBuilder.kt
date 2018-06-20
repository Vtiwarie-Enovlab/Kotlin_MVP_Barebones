package com.enovlab.yoop.ui.auth.verify

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */

@Module
abstract class VerificationBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeVerificationFragment(): VerificationFragment

    @Binds
    @IntoMap
    @ViewModelKey(VerificationViewModel::class)
    internal abstract fun bindVerificationViewModel(viewModel: VerificationViewModel): ViewModel
}