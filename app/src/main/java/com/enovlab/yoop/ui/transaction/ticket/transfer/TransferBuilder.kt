package com.enovlab.yoop.ui.transaction.ticket.transfer

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TransferBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTransferFragment(): TransferFragment

    @Binds
    @IntoMap
    @ViewModelKey(TransferViewModel::class)
    internal abstract fun bindTransferViewModel(viewModel: TransferViewModel): ViewModel

}