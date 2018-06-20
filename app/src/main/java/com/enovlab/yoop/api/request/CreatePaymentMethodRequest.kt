package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
data class CreatePaymentMethodRequest(@SerializedName("tokenId") val token: String?,
                                      val cardType: String?,
                                      @SerializedName("setDefault") val isDefault: Boolean)