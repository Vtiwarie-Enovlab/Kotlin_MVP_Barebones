package com.enovlab.yoop.api.response

import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.user.User
import com.google.gson.annotations.SerializedName

data class UserEvent(
    val event: Event,
    @SerializedName("userProfile") val user: User
)