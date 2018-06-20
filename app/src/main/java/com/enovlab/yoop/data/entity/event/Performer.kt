package com.enovlab.yoop.data.entity.event

import android.arch.persistence.room.*

/**
 * Created by mtosk on 3/9/2018.
 */

@Entity(
    tableName = "performers",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Performer(

    @PrimaryKey
    var id: String,

    var name: String?,

    @Embedded
    var defaultMedia: DefaultMedia?
)