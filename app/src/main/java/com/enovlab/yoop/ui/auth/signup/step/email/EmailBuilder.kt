package com.enovlab.yoop.ui.auth.signup.step.email

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by mtosk on 3/5/2018.
 */

@Module
abstract class EmailBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeEmailFragment(): EmailFragment

    @Binds
    @IntoMap
    @ViewModelKey(EmailViewModel::class)
    internal abstract fun bindEmailViewModel(viewModel: EmailViewModel): ViewModel
}