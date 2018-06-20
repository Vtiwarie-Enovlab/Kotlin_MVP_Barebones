package com.enovlab.yoop.data.entity.user

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.enums.UserAuthenticationState
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User (

    @PrimaryKey
    @SerializedName("emailAddress")
    var email: String,

    var firstName: String?,

    var lastName: String?,

    @SerializedName("profilePictureUrl")
    var photo: String?,

    var mobileNumber: String?,

    var authToken: String?,

    @SerializedName("userAuthenticationState")
    var authState: UserAuthenticationState?,

    @SerializedName("userLocale")
    var locale: String?,

    @SerializedName("profilePictureVerificationStatus")
    var photoVerified: Boolean?,

    @SerializedName("eventReady")
    var eventReady: Boolean?,

    @SerializedName("latestPictureVerificationDate")
    var verificationDate: Date?,

    @SerializedName("seenProfilePictureVerificationState")
    var verificationSeen: Boolean?,

    @Embedded
    var mobileCountry: MobileCountry?
) {
    @Ignore
    @SerializedName("paymentMeans")
    var paymentMethods: List<PaymentMethod>? = null
}