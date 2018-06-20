package com.enovlab.yoop.ui.payments.edit

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
abstract class EditPaymentsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeEditPaymentsFragment(): EditPaymentsFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditPaymentsViewModel::class)
    internal abstract fun bindEditPaymentsViewModel(viewModel: EditPaymentsViewModel): ViewModel
}