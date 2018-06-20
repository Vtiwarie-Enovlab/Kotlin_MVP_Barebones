package com.enovlab.yoop.api.request

import com.google.gson.annotations.SerializedName

/**
 * Created by mtosk on 3/14/2018.
 */
data class LocationFilterRequest(
    val cityId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("radiusInMeters") val radius: Long? = null)