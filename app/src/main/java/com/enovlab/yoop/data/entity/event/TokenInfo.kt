package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName

/**
 * Created by mtosk on 3/21/2018.
 */

@Entity(
    tableName = "token_info",
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
data class TokenInfo(

    @PrimaryKey
    @SerializedName("tokenId")
    var id: String,

    @ColumnInfo(name = "event_id")
    var eventId: String?,

    var row: String?,

    var seat: String?,

    var section: String?,

    @Embedded
    @SerializedName("offerResponse")
    var offer: Offer?,

    @Embedded
    var tokenAssignment: TokenAssignment?,

    var isAssignee: Boolean?,

    var selfAssigned: Boolean?
)