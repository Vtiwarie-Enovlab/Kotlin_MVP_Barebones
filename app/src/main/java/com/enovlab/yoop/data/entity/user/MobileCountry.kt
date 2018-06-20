package com.enovlab.yoop.data.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 12/20/2017.
 */

data class MobileCountry (

    var countryName: String?,

    @SerializedName("countryCodeISO2")
    var countryCode: String?,

    var phoneCode: String?
)
