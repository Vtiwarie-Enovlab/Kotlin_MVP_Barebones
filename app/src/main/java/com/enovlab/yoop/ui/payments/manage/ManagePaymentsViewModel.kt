package com.enovlab.yoop.ui.payments.manage

import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.payments.manage.adapter.PaymentItem
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

/**
 * @author vishaan
 */
class ManagePaymentsViewModel
@Inject constructor(private val userRepository: UserRepository) : StateViewModel<ManagePaymentsView>() {

    override fun start() {
        refresh()
    }

    internal fun refresh() {
        when {
            userRepository.isAuthorized() -> load { userRepository.refreshUser().toCompletable() }
        }
        observePayments()
    }

    internal fun observePayments() {
        //find the user's payment method to edit. If null, user is entering a new card
        if (userRepository.isAuthorized()) {
            disposables += userRepository.user().subscribe({ user ->

                var paymentItems = mutableListOf<PaymentItem>()
                user.paymentMethods?.forEach {
                    val expiryDate = ""+it.expirationMonth.toString().padStart(2, '0') + "/" + it.expirationYear.toString().slice(listOf(2, 3))
                    paymentItems.add(PaymentItem(PaymentItem.PaymentMethodItem.PaymentMethod(it.id, it.cardType, it.lastDigits, it.isDefault, expiryDate)))
                }
                paymentItems.add(PaymentItem(PaymentItem.PaymentMethodItem.AddNewPaymentMethod))

                view?.submitList(paymentItems)
            }, {
                Timber.e(it)
            })
        }
    }
}