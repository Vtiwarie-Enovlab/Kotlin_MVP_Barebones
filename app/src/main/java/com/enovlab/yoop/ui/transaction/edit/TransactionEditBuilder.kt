package com.enovlab.yoop.ui.transaction.edit

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TransactionEditBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransactionEditFragment(): TransactionEditFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransactionEditViewModel::class)
    internal abstract fun bindTransactionEditViewModel(viewModel: TransactionEditViewModel): ViewModel

}