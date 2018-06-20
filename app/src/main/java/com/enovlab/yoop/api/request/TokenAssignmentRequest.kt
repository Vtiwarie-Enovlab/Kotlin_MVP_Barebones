package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

data class TokenAssignmentRequest(
    @SerializedName("emailAddress") val email: String,
    val tokenIds: Array<String>
)