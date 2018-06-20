package com.enovlab.yoop.data.worker

import com.enovlab.yoop.api.request.NotificationsUpdateRequest
import timber.log.Timber

class NotificationsSyncWorker : BaseWorker() {

    override fun continueWork(): WorkerResult {
        val notificationDao = database.notificationDao()

        val notificationsToRead = notificationDao.getNotificationsToRead()
        val notificationsToArchive = notificationDao.getNotificationsToArchive()
        if (notificationsToRead.isEmpty() && notificationsToArchive.isEmpty())
            return WorkerResult.SUCCESS

        var result = WorkerResult.RETRY

        if (notificationsToRead.isNotEmpty()) {
            val ids = notificationsToRead.map { it.id }
            service.markNotificationsAsRead(NotificationsUpdateRequest(ids)).subscribe({
                notificationsToRead.forEach {
                    it.readSync = true
                }
                notificationDao.updateNotifications(notificationsToRead)

                result = WorkerResult.SUCCESS
            }, { error ->
                result = WorkerResult.RETRY
                Timber.e(error)
            })
        }

        if (notificationsToArchive.isNotEmpty()) {
            val ids = notificationsToArchive.map { it.id }
            service.markNotificationsAsArchived(NotificationsUpdateRequest(ids)).subscribe({
                notificationsToArchive.forEach {
                    it.archivedSync = true
                }
                notificationDao.updateNotifications(notificationsToArchive)

                result = WorkerResult.SUCCESS
            }, { error ->
                result = WorkerResult.RETRY
                Timber.e(error)
            })
        }

        return result
    }
}