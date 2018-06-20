package com.enovlab.yoop.data.query

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.enovlab.yoop.data.entity.event.*

/**
 * Created by Max Toskhoparan on 2/2/2018.
 */
class EventQuery {

    @Embedded
    lateinit var event: Event

    @Relation(parentColumn = "id", entityColumn = "event_id", entity = MarketplaceInfo::class)
    var marketplaceInfo: List<MarketplaceInfoQuery>? = null

    @Relation(parentColumn = "id", entityColumn = "event_id")
    var tokenInfo: List<TokenInfo>? = null

    @Relation(parentColumn = "id", entityColumn = "event_id")
    var media: List<EventMedia>? = null

    @Relation(parentColumn = "id", entityColumn = "event_id")
    var timelines: List<Timeline>? = null

    fun toEvent(): Event {
        event.marketplaceInfo = convertMarketplaceInfo()

        event.media = if (media == null || media?.isEmpty() == true) null else media

        event.timelines = if (timelines == null || timelines?.isEmpty() == true) null else timelines

        when {
            tokenInfo != null && tokenInfo!!.isNotEmpty() -> {
                event.tokenInfo = tokenInfo!!.filter { it.isAssignee == null || it.isAssignee == false }
                event.assigneeTokenInfo = tokenInfo!!.filter { it.isAssignee != null && it.isAssignee == true }
            }
            else -> {
                event.tokenInfo = null
                event.assigneeTokenInfo = null
            }
        }

        return event
    }

    private fun convertMarketplaceInfo(): List<MarketplaceInfo>? {
        if (marketplaceInfo == null || marketplaceInfo?.isEmpty() == true) return null

        val list = mutableListOf<MarketplaceInfo>()
        marketplaceInfo?.forEach {
            val converted = it.toMarketplaceInfo()
            if (converted != null) list.add(converted)
        }

        if (list.isEmpty()) return null

        return list
    }
}