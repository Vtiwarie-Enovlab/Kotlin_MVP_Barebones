package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.enums.Demand
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

@Entity(
    tableName = "marketplace_info",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["event_id"])
    ],
    foreignKeys = [
        ForeignKey(entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    ]
)
data class MarketplaceInfo (

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "event_id")
    var eventId: String?,

    @SerializedName("startDateTime")
    var startDate: Date?,

    @SerializedName("endDateTime")
    var endDate: Date?,

    @SerializedName("auctionStartDateTime")
    var auctionStartDate: Date?,

    @SerializedName("auctionEndDateTime")
    var auctionEndDate: Date?,

    @SerializedName("purchaseStartDateTime")
    var purchaseStartDate: Date?,

    @SerializedName("purchaseEndDateTime")
    var purchaseEndDate: Date?,

    @SerializedName("limit")
    var limitCount: Int?,

    var dualMarketplaceEnabled: Boolean?,

    var type: MarketplaceType?,

    @SerializedName("marketplaceDemand")
    var demand: Demand?,

    var userTicketLimitRemaining: Int?
) {
    @Ignore
    @SerializedName("offerGroupInformation")
    var offerGroups: List<OfferGroup>? = null
}
