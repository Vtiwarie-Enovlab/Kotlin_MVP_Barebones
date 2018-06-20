package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.user.UserInfo
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

@Entity(
    tableName = "events",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Event (

    @PrimaryKey
    var id: String,

    var name: String?,

    var shortName: String?,

    @SerializedName("optionalShortDescription")
    var shortDescription: String?,

    @SerializedName("optionalLongDescription")
    var longDescription: String?,

    @SerializedName("eventDate")
    var date: Date?,

    @SerializedName("timeZoneOffset")
    var timeZone: String?,

    var currency: String?,

    var locationName: String? ,

    @SerializedName("latitude")
    var locationLatitude: Double?,

    @SerializedName("longitude")
    var locationLongitude: Double?,

    var countryName: String?,

    var geoRegionName: String?,

    var geoRegionAbbreviation: String?,

    var cityName: String?,

    var addressLine1: String?,

    var addressLine2: String?,

    @SerializedName("postalCode")
    var zipCode: String?,

    var ticketLimit: String?,

    @SerializedName("currDrawEndDateTime")
    @ColumnInfo(name = "first_access_end_date")
    var firstAccessEndDate: Date?,

    @SerializedName("currLiveMarketEndDateTime")
    @ColumnInfo(name = "on_sale_end_date")
    var onSaleEndDate: Date?,

    var minQualifyingPrice: Double?,

    var earliestMarketplaceStartDateTime: Date?,

    var maxOfferGroupCountInActiveMarketplace: Int?,

    var discoverable: Boolean?,

    @ColumnInfo(name = "user_activity")
    var userActivity: Boolean?,

    @Embedded
    var defaultMedia: DefaultMedia?,

    @Embedded
    var seatMap: SeatMap?,

    @Embedded
    var nextMarketplace: NextMarketplace?,

    @ColumnInfo(name = "update_date")
    var updateDate: Date?,

    @Embedded
    var userInfo: UserInfo?,

    @ColumnInfo(name = "event_key")
    var userEventKey: String?,

    @ColumnInfo(name = "uuid_prefix")
    var userUUIDPrefix: String?,

    @ColumnInfo(name = "usher_uuid")
    var usherUUID: String?
) {
    @Ignore
    @SerializedName("marketplaceInformation")
    var marketplaceInfo: List<MarketplaceInfo>? = null

    @Ignore
    @SerializedName("tokenInformation")
    var tokenInfo: List<TokenInfo>? = null

    @Ignore
    @SerializedName("assigneeTokenInformation")
    var assigneeTokenInfo: List<TokenInfo>? = null

    @Ignore
    var media: List<EventMedia>? = null

    @Ignore
    var performers: List<Performer>? = null

    @Ignore
    @SerializedName("eventTimeline")
    var timelines: List<Timeline>? = null
}
