package com.enovlab.yoop.ui.main.mytickets.inbox

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class InboxBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeInboxFragment(): InboxFragment

    @Binds
    @IntoMap
    @ViewModelKey(InboxViewModel::class)
    internal abstract fun bindInboxViewModel(viewModel: InboxViewModel): ViewModel
}