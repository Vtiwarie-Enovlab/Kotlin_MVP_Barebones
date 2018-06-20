package com.enovlab.yoop.data.entity

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.enums.CardType
import com.enovlab.yoop.data.entity.user.User
import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Entity(
    tableName = "payment_methods",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["user_id"])
    ],
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["email"],
            childColumns = ["user_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    ]
)
data class PaymentMethod (

    @PrimaryKey
    @SerializedName("paymentMethodId")
    var id: String,

    @ColumnInfo(name = "user_id")
    var userId: String?,

    var cardType: CardType?,

    var cardHolderName: String?,

    var lastDigits: String?,

    var country: String?,

    var expirationMonth: Int?,

    var expirationYear: Int?,

    @SerializedName("postalCode")
    var zipCode: String?,

    @ColumnInfo(name = "is_default")
    @SerializedName("defaultMeans")
    var isDefault: Boolean?
)
