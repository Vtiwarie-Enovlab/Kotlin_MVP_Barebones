package com.enovlab.yoop.ui.transaction.confirmation

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TransactionConfirmationBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransactionConfirmationFragment(): TransactionConfirmationFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransactionConfirmationViewModel::class)
    internal abstract fun bindTransactionConfirmationViewModel(viewModel: TransactionConfirmationViewModel): ViewModel

}