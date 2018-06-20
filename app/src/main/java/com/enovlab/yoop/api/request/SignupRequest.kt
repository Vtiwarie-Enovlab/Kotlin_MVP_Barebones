package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/8/2018.
 */
data class SignupRequest (@SerializedName("emailAddress") val email: String,
                          val password: String,
                          val firstName: String,
                          val lastName: String,
                          @SerializedName("userLocale") val locale: String)