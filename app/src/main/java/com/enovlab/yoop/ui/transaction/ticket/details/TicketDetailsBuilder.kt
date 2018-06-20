package com.enovlab.yoop.ui.transaction.ticket.details

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TicketDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeTicketDetailsFragment(): TicketDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(TicketDetailsViewModel::class)
    internal abstract fun bindTicketDetailsViewModel(viewModel: TicketDetailsViewModel): ViewModel

}