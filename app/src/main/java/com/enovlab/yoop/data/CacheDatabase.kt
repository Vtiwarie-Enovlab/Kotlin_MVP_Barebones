package com.enovlab.yoop.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.enovlab.yoop.data.entity.MovieEntity

@Database(entities = [
    MovieEntity::class
], version = 1)
@TypeConverters(AppTypeConverters::class)
abstract class CacheDatabase : RoomDatabase()