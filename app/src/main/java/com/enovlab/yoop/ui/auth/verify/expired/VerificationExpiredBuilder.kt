package com.enovlab.yoop.ui.auth.verify.expired

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
abstract class VerificationExpiredBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeVerificationExpiredFragment(): VerificationExpiredFragment

    @Binds
    @IntoMap
    @ViewModelKey(VerificationExpiredViewModel::class)
    internal abstract fun bindVerificationExpiredViewModel(viewModel: VerificationExpiredViewModel): ViewModel
}