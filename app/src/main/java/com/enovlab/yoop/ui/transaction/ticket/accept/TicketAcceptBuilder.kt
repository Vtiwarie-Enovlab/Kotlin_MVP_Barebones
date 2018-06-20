package com.enovlab.yoop.ui.transaction.ticket.accept

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TicketAcceptBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTicketAcceptFragment(): TicketAcceptFragment

    @Binds
    @IntoMap
    @ViewModelKey(TicketAcceptViewModel::class)
    internal abstract fun bindTicketAcceptViewModel(viewModel: TicketAcceptViewModel): ViewModel

}