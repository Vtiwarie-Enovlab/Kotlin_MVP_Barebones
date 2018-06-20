package com.enovlab.yoop.inject

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import com.enovlab.yoop.data.YoopDatabase
import com.enovlab.yoop.data.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): YoopDatabase {
        val builder = Room.databaseBuilder(context, YoopDatabase::class.java, "yoop.db")
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Provides
    fun provideEventDao(db: YoopDatabase): EventDao = db.eventDao()

    @Provides
    fun provideNotificationDao(db: YoopDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideUserDao(db: YoopDatabase): UserDao = db.userDao()

    @Provides
    fun providePaymentMethodDao(db: YoopDatabase): PaymentMethodDao = db.paymentMethodDao()

    @Provides
    fun provideFilterDao(db: YoopDatabase): FilterDao = db.filterDao()
}
