package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index

/**
 * Created by mtosk on 3/12/2018.
 */

@Entity(
    tableName = "event_performers",
    primaryKeys = ["event_id", "performer_id"],
    indices = [
        Index(value = ["event_id"]),
        Index(value = ["performer_id"])
    ],
    foreignKeys = [
        ForeignKey(entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["event_id"]),
        ForeignKey(entity = Performer::class,
            parentColumns = ["id"],
            childColumns = ["performer_id"])
    ])
data class EventPerformer(
    @ColumnInfo(name = "event_id")
    var eventId: String,

    @ColumnInfo(name = "performer_id")
    var performerId: String
)