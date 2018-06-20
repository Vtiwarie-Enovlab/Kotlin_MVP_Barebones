package com.enovlab.yoop.data.entity.event

import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.google.gson.annotations.SerializedName
import java.util.*

data class NextMarketplace(
    @SerializedName("id")
    val marketplaceId: String?,

    val type: MarketplaceType?,

    @SerializedName("startDateTime")
    val startDate: Date?,

    @SerializedName("endDateTime")
    val endDate: Date?
)