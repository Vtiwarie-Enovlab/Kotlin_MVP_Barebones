package com.enovlab.yoop.ui.transaction.details

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
abstract class TransactionDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransactionDetailsFragment(): TransactionDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransactionDetailsViewModel::class)
    internal abstract fun bindTransactionDetailsViewModel(viewModel: TransactionDetailsViewModel): ViewModel

}
