package com.enovlab.yoop.api.response.maps

import com.google.gson.annotations.SerializedName

data class MapResult(
    @SerializedName("address_components")
    val addressComponents: List<MapAddressComponent>
)
