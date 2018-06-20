package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/4/2018.
 */
data class LoginRequest(@SerializedName("emailAddress") val email: String,
                        val password: String)