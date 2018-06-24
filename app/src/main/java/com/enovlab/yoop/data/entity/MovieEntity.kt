package com.enovlab.yoop.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "movies",
    indices = [
        (Index(value = ["id"], unique = true))
    ])
data class MovieEntity(

    @PrimaryKey
    var id: String
)