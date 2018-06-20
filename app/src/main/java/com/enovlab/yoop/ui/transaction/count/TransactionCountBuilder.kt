package com.enovlab.yoop.ui.transaction.count

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TransactionCountBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransactionCountFragment(): TransactionCountFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransactionCountViewModel::class)
    internal abstract fun bindTransactionCountViewModel(viewModel: TransactionCountViewModel): ViewModel

}