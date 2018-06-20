package com.enovlab.yoop.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.enovlab.yoop.data.dao.*
import com.enovlab.yoop.data.entity.*
import com.enovlab.yoop.data.entity.event.*
import com.enovlab.yoop.data.entity.notification.Notification
import com.enovlab.yoop.data.entity.user.User

/**
 * Created by Max Toskhoparan on 2/2/2018.
 */
@Database(entities = [
    Event::class,
    EventMedia::class,
    MarketplaceInfo::class,
    OfferGroup::class,
    TokenInfo::class,
    User::class,
    PaymentMethod::class,
    Notification::class,
    Performer::class,
    EventPerformer::class,
    City::class,
    FilterOptions::class,
    Timeline::class
], version = 1)
@TypeConverters(YoopTypeConverters::class)
abstract class YoopDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun filterDao(): FilterDao
}