package com.enovlab.yoop.data.dao

import android.arch.persistence.room.*
import com.enovlab.yoop.api.response.NotificationGroup
import com.enovlab.yoop.data.entity.notification.Notification
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Max Toskhoparan on 12/11/2017.
 */

@Dao
abstract class NotificationDao {

    @Query("SELECT * FROM notifications WHERE (archived IS NULL OR archived = 0)")
    abstract fun getNotifications(): Flowable<List<Notification>>

    @Query("SELECT * FROM notifications WHERE id IN(:ids)")
    abstract fun getNotifications(ids: List<String>): Flowable<List<Notification>>

    @Query("SELECT * FROM notifications WHERE (archived IS NULL OR archived = 0) AND (read IS NULL OR read = 0)")
    abstract fun getUnreadNotifications(): Flowable<List<Notification>>

    @Query("SELECT * FROM notifications WHERE id = :id")
    abstract fun getNotification(id: String): Single<Notification>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateNotification(notification: Notification)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateNotifications(notifications: List<Notification>)

    @Query("SELECT * FROM notifications WHERE read = 1 AND read_sync = 0")
    abstract fun getNotificationsToRead(): List<Notification>

    @Query("SELECT * FROM notifications WHERE archived = 1 AND archived_sync = 0")
    abstract fun getNotificationsToArchive(): List<Notification>

    @Query("SELECT * FROM notifications")
    abstract fun getNotificationsSync(): List<Notification>

    @Delete
    abstract fun deleteNotifications(notifications: List<Notification>)

    @Transaction
    open fun saveNotifications(notifications: List<NotificationGroup>) {
        syncNotifications(notifications)
    }

    @Query("DELETE FROM notifications")
    abstract fun deleteNotifications()

    @Query("DELETE FROM notifications WHERE id NOT IN(:ids)")
    protected abstract fun deleteIrrelevantNotifications(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveNotificationsInternal(notifications: List<Notification>)

    private fun syncNotifications(groups: List<NotificationGroup>) {
        if (groups.isEmpty()) return

        val refreshedNotifications = mutableListOf<Notification>()
        groups.forEach { group ->
            val notifications = group.notifications
            if (notifications != null && notifications.isNotEmpty()) {
                if (group.event != null) {
                    notifications.forEach {
                        it.event = group.event
                    }
                }
                refreshedNotifications.addAll(notifications)
            }
        }

        deleteIrrelevantNotifications(refreshedNotifications.map { it.id })

        val cachedNotifications = getNotificationsSync()

        refreshedNotifications.forEach { notification ->
            val cachedNotification = cachedNotifications.find { it.id == notification.id }
            if (cachedNotification != null) {
                notification.read = cachedNotification.read
                notification.readSync = cachedNotification.readSync
                notification.archived = cachedNotification.archived
                notification.archivedSync = cachedNotification.archivedSync
            }
        }

        saveNotificationsInternal(refreshedNotifications)
    }
}