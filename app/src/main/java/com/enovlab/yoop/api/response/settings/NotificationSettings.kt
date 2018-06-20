package com.enovlab.yoop.api.response.settings

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/29/2018.
 */
data class NotificationSettings(val title: String,
                                val description: String,
                                var shouldSend: Boolean,
                                @SerializedName("notificationDeliveryType") val type: Type,
                                @SerializedName("notificationGroup") val group: Group) {

    enum class Type {
        PUSH, EMAIL, SMS, INBOX
    }

    enum class Group {
        MARKETPLACE, PAYMENT, SYSTEM
    }
}