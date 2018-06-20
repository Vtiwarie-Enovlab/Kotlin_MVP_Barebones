package com.enovlab.yoop.data.dao

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.PaymentMethod

import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Dao
abstract class PaymentMethodDao {

    @Query("SELECT * FROM payment_methods")
    abstract fun getPaymentMethods(): Flowable<List<PaymentMethod>>

    @Query("SELECT * FROM payment_methods WHERE is_default = 0")
    abstract fun getNonDefaultPaymentMethods(): Flowable<List<PaymentMethod>>

    @Query("SELECT COUNT(*) FROM payment_methods")
    abstract fun getPaymentMethodsCount(): Single<Int>

    @Transaction
    open fun savePaymentMethods(paymentMethods: List<PaymentMethod>) {
        syncPaymentMethods(paymentMethods)
    }

    @Query("SELECT * FROM payment_methods WHERE id = :id")
    abstract fun getPaymentMethod(id: String): Flowable<PaymentMethod>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun savePaymentMethod(paymentMethod: PaymentMethod)

    @Query("DELETE FROM payment_methods WHERE id = :id")
    abstract fun deletePaymentMethod(id: String)

    @Query("DELETE FROM payment_methods")
    abstract fun delete()

    @Query("DELETE FROM payment_methods WHERE id NOT IN(:ids)")
    protected abstract fun deleteIrrelevantPaymentMethods(ids: List<String>)

    @Query("SELECT * FROM payment_methods WHERE id IN(:ids)")
    protected abstract fun getRelevantPaymentMethods(ids: List<String>): List<PaymentMethod>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun savePaymentMethodsInternal(paymentMethods: List<PaymentMethod>)

    private fun syncPaymentMethods(paymentMethods: List<PaymentMethod>) {
        val ids = paymentMethods.map { it.id }
        deleteIrrelevantPaymentMethods(ids)

        val relevant = getRelevantPaymentMethods(ids)

        paymentMethods.forEach { payment ->
            val cached = relevant.find { it.id == payment.id }
            if (cached != null) {
                payment.userId = cached.userId
            }
        }

        savePaymentMethodsInternal(paymentMethods)
    }
}
