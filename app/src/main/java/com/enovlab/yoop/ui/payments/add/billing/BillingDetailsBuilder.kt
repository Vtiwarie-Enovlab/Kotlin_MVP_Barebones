package com.enovlab.yoop.ui.payments.add.billing

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
abstract class BillingDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeBillingDetailsFragment(): BillingDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(BillingDetailsViewModel::class)
    internal abstract fun bindBillingDetailsViewModel(viewModel: BillingDetailsViewModel): ViewModel
}