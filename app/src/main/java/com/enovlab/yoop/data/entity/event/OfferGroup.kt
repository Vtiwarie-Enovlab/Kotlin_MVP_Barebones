package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.enums.Demand
import com.enovlab.yoop.data.entity.enums.SeatConfigurationType

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

@Entity(
    tableName = "offer_groups",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["marketplace_id"])
    ],
    foreignKeys = [
        ForeignKey(entity = MarketplaceInfo::class,
            parentColumns = ["id"],
            childColumns = ["marketplace_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    ]
)
data class OfferGroup (

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "marketplace_id")
    var marketplaceId: String?,

    var numberOfTokens: Int?,

    var numberOfPeopleOnList: Int?,

    var purchasePrice: Double?,

    var unsoldInventory: Int?,

    var reservePrice: Double?,

    var averageOfferPrice: Double?,

    var minQualifyingPrice: Double?,

    var seatConfigurationType: SeatConfigurationType?,

    var description: String?,

    var demand: Demand?,

    @Embedded
    var offer: Offer?
)