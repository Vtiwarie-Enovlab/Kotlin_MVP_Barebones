package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.enums.EventMediaType
import com.google.gson.annotations.SerializedName

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

@Entity(
    tableName = "event_media",
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
data class EventMedia (

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "event_id")
    var eventId: String?,

    @SerializedName("mediaType")
    var type: EventMediaType?,

    var url: String?
)
