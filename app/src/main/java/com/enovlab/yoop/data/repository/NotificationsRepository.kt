package com.enovlab.yoop.data.repository

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.response.NotificationGroup
import com.enovlab.yoop.data.dao.NotificationDao
import com.enovlab.yoop.data.entity.notification.Notification
import com.enovlab.yoop.data.worker.NotificationsSyncWorker
import com.enovlab.yoop.utils.RxSchedulers
import com.enovlab.yoop.utils.ext.toCompletable
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 12/11/2017.
 */
class NotificationsRepository
@Inject constructor(private val yoopService: YoopService,
                    private val notificationDao: NotificationDao,
                    private val schedulers: RxSchedulers) {

    fun observeNotifications(): Flowable<List<Notification>> {
        return notificationDao.getNotifications()
            .subscribeOn(schedulers.disk)
            .distinctUntilChanged()
    }

    fun observeUnreadNotifications(): Flowable<Int> {
        return notificationDao.getUnreadNotifications()
            .subscribeOn(schedulers.disk)
            .map { it.size }
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    fun loadNotifications(): Flowable<List<NotificationGroup>> {
        return yoopService.getNotifications()
            .subscribeOn(schedulers.network)
            .map { it.notificationGroups }
            .observeOn(schedulers.disk)
            .doOnNext(notificationDao::saveNotifications)
    }

    fun readNotification(id: String): Completable {
        return notificationDao.getNotification(id)
            .subscribeOn(schedulers.disk)
            .map {
                it.read = true
                it.readSync = false
                it
            }
            .observeOn(schedulers.disk)
            .doOnSuccess(notificationDao::updateNotification)
            .toCompletable()
            .observeOn(schedulers.main)
    }

    fun archiveNotifications(ids: List<String>): Completable {
        return notificationDao.getNotifications(ids)
            .subscribeOn(schedulers.disk)
            .map { list ->
                list.forEach { notification ->
                    notification.archived = true
                    notification.archivedSync = false
                }
                list
            }
            .observeOn(schedulers.disk)
            .doOnNext(notificationDao::updateNotifications)
            .toCompletable()
            .observeOn(schedulers.main)
    }

    fun syncNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<NotificationsSyncWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(WORK_TAG_SYNC_NOTIF)
            .build()
        WorkManager.getInstance().cancelAllWorkByTag(WORK_TAG_SYNC_NOTIF)
        WorkManager.getInstance().enqueue(request)
    }

    companion object {
        private const val WORK_TAG_SYNC_NOTIF = "WORK_TAG_SYNC_NOTIF"
    }
}