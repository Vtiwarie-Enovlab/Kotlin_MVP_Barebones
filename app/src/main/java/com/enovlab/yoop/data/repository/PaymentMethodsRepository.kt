package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.PaysafeService
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.CreatePaymentMethodRequest
import com.enovlab.yoop.api.request.SingleUseTokenRequest
import com.enovlab.yoop.api.request.UpdatePaymentMethodRequest
import com.enovlab.yoop.api.response.payment.BillingAddress
import com.enovlab.yoop.api.response.payment.Card
import com.enovlab.yoop.api.response.payment.CardExpiry
import com.enovlab.yoop.data.dao.PaymentMethodDao
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/11/2018.
 */
class PaymentMethodsRepository
@Inject constructor(private val yoopService: YoopService,
                    private val paysafeService: PaysafeService,
                    private val paymentMethodDao: PaymentMethodDao,
                    private val schedulers: RxSchedulers) {

    fun paymentMethods(): Flowable<List<PaymentMethod>> {
        return paymentMethodDao.getPaymentMethods()
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    fun nonDefaultPaymentMethods() = paymentMethodDao.getNonDefaultPaymentMethods()

    fun paymentMethod(id: String) = paymentMethodDao.getPaymentMethod(id)

    fun refreshPaymentMethods(): Flowable<List<PaymentMethod>> {
        return yoopService.getPaymentMethods()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnNext(this::savePaymentMethods)
    }

    fun setDefaultPaymentMethod(id: String): Flowable<List<PaymentMethod>> {
        return yoopService.setDefaultPaymentMethod(id)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnNext(this::savePaymentMethods)
            .observeOn(schedulers.main)
    }

    fun deletePaymentMethod(id: String): Flowable<List<PaymentMethod>> {
        return yoopService.deletePaymentMethod(id)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnNext {
                removePaymentMethod(id)
                savePaymentMethods(it)
            }
    }

    fun setDefaultAndDeletePaymentMethod(defaultId: String, deleteId: String): Flowable<List<PaymentMethod>> {
        return yoopService.setDefaultPaymentMethod(defaultId)
            .subscribeOn(schedulers.network)
            .flatMap { yoopService.deletePaymentMethod(deleteId) }
            .observeOn(schedulers.disk)
            .doOnNext {
                removePaymentMethod(deleteId)
                savePaymentMethods(it)
            }
    }

    fun createPaymentMethod(cardNum: String, holderName: String, cvv: String,
                            month: Int, year: Int, country: String, zip: String,
                            isDefault: Boolean): Single<PaymentMethod> {

        val request = SingleUseTokenRequest(Card().apply {
            this.cardNum = cardNum
            this.holderName = holderName
            this.cvv = cvv

            cardExpiry = CardExpiry().apply {
                this.month = month
                this.year = year
            }

            billingAddress = BillingAddress().apply {
                this.country = country
                this.zip = zip
            }
        })

        return paysafeService.getToken(request)
            .subscribeOn(schedulers.network)
            .flatMap { yoopService.createPaymentMethod(
                CreatePaymentMethodRequest(it.paymentToken, it.card?.cardType, isDefault)) }
            .observeOn(schedulers.disk)
            .doOnSuccess(this::savePaymentMethod)
    }

    fun updatePaymentMethod(id: String, cardType: String, country: String, zipCode: String, isDefault: Boolean): Single<PaymentMethod> {
        return yoopService.updatePaymentMethod(UpdatePaymentMethodRequest(id, cardType, country, zipCode, isDefault))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess(this::savePaymentMethod)
    }

    fun hasPayments(): Single<Boolean> {
        return paymentMethodDao.getPaymentMethodsCount()
            .map { it > 0 }
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    private fun savePaymentMethods(payments: List<PaymentMethod>) {
        paymentMethodDao.savePaymentMethods(payments)
    }

    private fun savePaymentMethod(payment: PaymentMethod) {
        paymentMethodDao.savePaymentMethod(payment)
    }

    private fun removePaymentMethod(id: String) {
        paymentMethodDao.deletePaymentMethod(id)
    }
}