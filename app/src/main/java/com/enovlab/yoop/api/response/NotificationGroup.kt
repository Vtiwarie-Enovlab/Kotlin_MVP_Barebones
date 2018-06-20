package com.enovlab.yoop.api.response

import com.enovlab.yoop.data.entity.notification.EventInfo
import com.enovlab.yoop.data.entity.notification.Notification
import com.google.gson.annotations.SerializedName

data class NotificationGroup(

    @SerializedName("eventInfo")
    var event: EventInfo?,

    @SerializedName("notificationItemResponses")
    var notifications: List<Notification>?
)