package com.enovlab.yoop.data.query

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.TokenInfo

/**
 * Created by Max Toskhoparan on 2/2/2018.
 */
class EventTicketsQuery {

    @Embedded
    lateinit var event: Event

    @Relation(parentColumn = "id", entityColumn = "event_id")
    var tokenInfo: List<TokenInfo>? = null

    fun toEvent(): Event {
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
}