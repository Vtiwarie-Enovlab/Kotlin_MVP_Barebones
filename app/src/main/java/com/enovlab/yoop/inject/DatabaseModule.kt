package com.enovlab.yoop.inject

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import com.enovlab.yoop.data.CacheDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): CacheDatabase {
        val builder = Room.databaseBuilder(context, CacheDatabase::class.java, "yoop.db")
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }
}
