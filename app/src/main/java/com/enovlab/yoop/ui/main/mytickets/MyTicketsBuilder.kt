package com.enovlab.yoop.ui.main.mytickets

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxBuilder
import com.enovlab.yoop.ui.main.mytickets.requested.RequestedBuilder
import com.enovlab.yoop.ui.main.mytickets.secured.SecuredBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MyTicketsBuilder {

    @ContributesAndroidInjector(modules = [
        SecuredBuilder::class,
        RequestedBuilder::class,
        InboxBuilder::class
    ])
    internal abstract fun contributeMyTicketsFragment(): MyTicketsFragment

    @Binds
    @IntoMap
    @ViewModelKey(MyTicketsViewModel::class)
    internal abstract fun bindMyTicketsViewModel(viewModel: MyTicketsViewModel): ViewModel
}