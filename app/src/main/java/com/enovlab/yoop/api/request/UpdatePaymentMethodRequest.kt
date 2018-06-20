package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
data class UpdatePaymentMethodRequest(@SerializedName("paymentMethodId") val id: String,
                                      val cardType: String? = null,
                                      @SerializedName("countryCode") val country: String? = null,
                                      @SerializedName("postalCode") val zipCode: String? = null,
                                      @SerializedName("setDefault") val isDefault: Boolean? = null)