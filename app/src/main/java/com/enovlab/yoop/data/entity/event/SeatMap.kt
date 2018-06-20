package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.ColumnInfo

/**
 * Created by Max Toskhoparan on 11/29/2017.
 */

data class SeatMap (

    @ColumnInfo(name = "seat_map_url")
    var url: String?,

    @ColumnInfo(name = "seat_map_short_description")
    var shortDescription: String?,

    @ColumnInfo(name = "seat_map_long_description")
    var longDescription: String?,

    @ColumnInfo(name = "seat_map_display_number")
    var displayNumber: Int?
)
