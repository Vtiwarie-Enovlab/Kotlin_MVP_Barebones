package com.enovlab.yoop.ui.payments

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.payments.add.billing.BillingDetailsBuilder
import com.enovlab.yoop.ui.payments.add.card.CardDetailsBuilder
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainBuilder
import com.enovlab.yoop.ui.payments.edit.EditPaymentsBuilder
import com.enovlab.yoop.ui.payments.manage.ManagePaymentsBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * @author vishaan
 */

@Module
abstract class PaymentsBuilder {

    @ContributesAndroidInjector(modules = [
        PaymentsMainBuilder::class,
        BillingDetailsBuilder::class,
        CardDetailsBuilder::class,
        EditPaymentsBuilder::class,
        ManagePaymentsBuilder::class
    ])
    internal abstract fun contributePaymentsActivity(): PaymentsActivity

    @Binds
    @IntoMap
    @ViewModelKey(PaymentsNavigator::class)
    internal abstract fun bindPaymentsNavigator(navigator: PaymentsNavigator): ViewModel
}
