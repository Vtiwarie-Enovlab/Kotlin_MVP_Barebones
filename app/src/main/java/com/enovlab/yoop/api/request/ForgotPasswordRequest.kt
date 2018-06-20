package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/8/2018.
 */
data class ForgotPasswordRequest(@SerializedName("emailAddress") val email: String)