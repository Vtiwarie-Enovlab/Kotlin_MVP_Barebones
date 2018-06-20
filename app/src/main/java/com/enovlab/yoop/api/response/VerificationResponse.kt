package com.enovlab.yoop.api.response

import com.google.gson.annotations.SerializedName

/**
 * Created by mtosk on 3/8/2018.
 */
data class VerificationResponse(@SerializedName("emailAddress") val email: String)