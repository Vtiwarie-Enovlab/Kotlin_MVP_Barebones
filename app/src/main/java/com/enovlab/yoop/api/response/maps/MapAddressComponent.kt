package com.enovlab.yoop.api.response.maps

import com.google.gson.annotations.SerializedName

/**
 * Created by mtosk on 3/13/2018.
 */
data class MapAddressComponent(
    @SerializedName("long_name") val longName: String,
    @SerializedName("short_name") val shortName: String,
    val types: List<String>
)