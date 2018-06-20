package com.enovlab.yoop.api.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 12/11/2017.
 */
data class NotificationsResponse (

    @SerializedName("notificationGroupResponses")
    val notificationGroups: List<NotificationGroup>
)