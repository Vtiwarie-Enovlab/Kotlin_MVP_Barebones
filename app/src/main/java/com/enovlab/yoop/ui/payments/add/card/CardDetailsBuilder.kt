package com.enovlab.yoop.ui.payments.add.card

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
abstract class CardDetailsBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeCardDetailsFragment(): CardDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(CardDetailsViewModel::class)
    internal abstract fun bindCardDetailsViewModel(viewModel: CardDetailsViewModel): ViewModel
}