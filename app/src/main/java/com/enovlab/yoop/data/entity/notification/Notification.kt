package com.enovlab.yoop.data.entity.notification

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.enums.NotificationType
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Max Toskhoparan on 12/11/2017.
 */

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Notification (

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "event_id")
    var eventId: String?,

    @SerializedName("notificationType")
    var type: NotificationType?,

    var body: String?,

    @SerializedName("deepLinkUrl")
    var deepLink: String?,

    @SerializedName("createdTimestamp")
    var createDate: Date?,

    var read: Boolean?,

    @ColumnInfo(name = "read_sync")
    var readSync: Boolean?,

    var archived: Boolean?,

    @ColumnInfo(name = "archived_sync")
    var archivedSync: Boolean?,

    @Embedded
    var event: EventInfo?
)