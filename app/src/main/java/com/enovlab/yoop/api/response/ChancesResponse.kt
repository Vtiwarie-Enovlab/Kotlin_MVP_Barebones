package com.enovlab.yoop.api.response

import com.google.gson.annotations.SerializedName

data class ChancesResponse(

    @SerializedName("chanceMapper")
    val chances: Chances,

    val token: String

) {
    data class Chances(
        val poor: Double? = null,
        val low: Double? = null,
        val good: Double? = null,
        val great: Double? = null
    )
}