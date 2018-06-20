package com.enovlab.yoop.data.query

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.enovlab.yoop.data.entity.event.MarketplaceInfo
import com.enovlab.yoop.data.entity.event.OfferGroup

/**
 * Created by Max Toskhoparan on 2/2/2018.
 */
class MarketplaceInfoQuery {

    @Embedded
    var marketplaceInfo: MarketplaceInfo? = null

    @Relation(parentColumn = "id", entityColumn = "marketplace_id")
    var offerGroups: List<OfferGroup>? = null

    fun toMarketplaceInfo(): MarketplaceInfo? {
        marketplaceInfo?.offerGroups = if (offerGroups == null || offerGroups?.isEmpty() == true) null else offerGroups
        return marketplaceInfo
    }
}