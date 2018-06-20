package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName
import com.enovlab.yoop.api.response.settings.NotificationSettings

/**
 * Created by Max Toskhoparan on 1/29/2018.
 */
data class NotificationSettingsUpdateRequest(
    @SerializedName("notificationDeliveryType") val type: NotificationSettings.Type,
    @SerializedName("notificationGroup") val group: NotificationSettings.Group,
    val shouldSend: Boolean)