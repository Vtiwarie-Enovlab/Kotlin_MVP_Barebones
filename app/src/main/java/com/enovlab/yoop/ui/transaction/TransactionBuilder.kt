package com.enovlab.yoop.ui.transaction

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.inject.viewmodel.ViewModelKey
import com.enovlab.yoop.ui.transaction.confirmation.TransactionConfirmationBuilder
import com.enovlab.yoop.ui.transaction.count.TransactionCountBuilder
import com.enovlab.yoop.ui.transaction.details.TransactionDetailsBuilder
import com.enovlab.yoop.ui.transaction.edit.TransactionEditBuilder
import com.enovlab.yoop.ui.transaction.review.TransactionReviewBuilder
import com.enovlab.yoop.ui.transaction.ticket.accept.TicketAcceptBuilder
import com.enovlab.yoop.ui.transaction.ticket.details.TicketDetailsBuilder
import com.enovlab.yoop.ui.transaction.ticket.transfer.TransferBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
abstract class TransactionBuilder {

    @ContributesAndroidInjector(modules = [
        TransactionDetailsBuilder::class,
        TransactionCountBuilder::class,
        TransactionReviewBuilder::class,
        TransactionConfirmationBuilder::class,
        TransactionEditBuilder::class,
        TicketDetailsBuilder::class,
        TransferBuilder::class,
        TicketAcceptBuilder::class
    ])
    internal abstract fun contributeTransactionActivity(): TransactionActivity

    @Binds
    @IntoMap
    @ViewModelKey(TransactionNavigator::class)
    internal abstract fun bindTransactionNavigator(navigator: TransactionNavigator): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    internal abstract fun bindTransactionViewModel(viewModel: TransactionViewModel): ViewModel
}
