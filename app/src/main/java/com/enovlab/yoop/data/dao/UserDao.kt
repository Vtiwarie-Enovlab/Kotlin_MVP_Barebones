package com.enovlab.yoop.data.dao

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.user.User
import com.enovlab.yoop.data.query.UserQuery
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Dao
abstract class UserDao  {

    @Transaction
    @Query("SELECT * FROM users LIMIT 1")
    abstract fun getUser(): Flowable<UserQuery>

    @Transaction
    open fun saveUser(user: User) {
        saveUserAndPayments(user)
    }

    @Query("DELETE FROM users")
    abstract fun deleteUser()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveUserInternal(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun savePaymentMethodInternal(paymentMethod: PaymentMethod)

    private fun saveUserAndPayments(user: User) {
        saveUserInternal(user)

        user.paymentMethods?.forEach {
            it.userId = user.email
            savePaymentMethodInternal(it)
        }
    }
}
