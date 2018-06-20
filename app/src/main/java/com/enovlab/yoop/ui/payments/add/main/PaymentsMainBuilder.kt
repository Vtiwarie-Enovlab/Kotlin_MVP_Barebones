package com.enovlab.yoop.ui.payments.add.main

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * @author vishaan
 */
@Module
abstract class PaymentsMainBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributePaymentsMainFragment(): PaymentsMainFragment

    @Binds
    @IntoMap
    @ViewModelKey(PaymentsMainViewModel::class)
    internal abstract fun bindPaymentsMainViewModel(viewModel: PaymentsMainViewModel): ViewModel
}