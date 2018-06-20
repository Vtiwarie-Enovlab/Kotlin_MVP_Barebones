package com.enovlab.yoop.ui.transaction.review

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TransactionReviewBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransactionReviewFragment(): TransactionReviewFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransactionReviewViewModel::class)
    internal abstract fun bindTransactionReviewViewModel(viewModel: TransactionReviewViewModel): ViewModel

}