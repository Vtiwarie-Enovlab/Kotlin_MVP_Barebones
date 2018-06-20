package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*

@Entity(
    tableName = "time_line",
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
data class Timeline(

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "event_id")
    var eventId: String?,

    var title: String?,

    var description: String?,

    var rank: Int?
)