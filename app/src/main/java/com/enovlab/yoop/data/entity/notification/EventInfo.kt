package com.enovlab.yoop.data.entity.notification

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import com.enovlab.yoop.data.entity.event.DefaultMedia
import com.enovlab.yoop.data.entity.event.Performer
import com.google.gson.annotations.SerializedName
import java.util.*

data class EventInfo (

    @ColumnInfo(name = "event_name")
    @SerializedName("shortName")
    var name: String?,

    @ColumnInfo(name = "event_date")
    @SerializedName("eventDate")
    var date: Date?,

    @Embedded
    var defaultMedia: DefaultMedia?
)