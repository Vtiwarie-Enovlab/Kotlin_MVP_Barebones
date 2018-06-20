package com.enovlab.yoop.data.query

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.user.User

class UserQuery {

    @Embedded
    lateinit var user: User

    @Relation(parentColumn = "email", entityColumn = "user_id")
    var paymentMethods: List<PaymentMethod>? = null

    fun toUser(): User {
        user.paymentMethods = if (paymentMethods == null || paymentMethods?.isEmpty() == true) null else paymentMethods

        return user
    }
}