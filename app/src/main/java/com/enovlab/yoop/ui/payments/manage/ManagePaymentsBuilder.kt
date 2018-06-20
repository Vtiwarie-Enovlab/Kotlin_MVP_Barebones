package com.enovlab.yoop.ui.payments.manage

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * @author vishaan
 */
@Module
abstract class ManagePaymentsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeManagePaymentsFragment(): ManagePaymentsFragment

    @Binds
    @IntoMap
    @ViewModelKey(ManagePaymentsViewModel::class)
    internal abstract fun bindManagePaymentsViewModel(viewModel: ManagePaymentsViewModel): ViewModel
}