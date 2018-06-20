package com.enovlab.yoop.ui.main.mytickets.inbox

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.data.entity.notification.Notification
import com.enovlab.yoop.data.repository.NotificationsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.EventItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem.*
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InboxViewModel
@Inject constructor(private val repository: NotificationsRepository) : StateViewModel<InboxView>() {

    override fun start() {
        if (preferences.authToken != null) {
            observeNotifications()
            load { repository.loadNotifications().toCompletable() }

            view?.showRefreshEnabled(true)
            view?.showNotAuthorized(false)

            repository.syncNotifications()
        } else {
            view?.showRefreshEnabled(false)
            view?.showNoNotifications(true)
            view?.showNotAuthorized(true)
            view?.showLegalLinks(BuildConfig.LINK_TERMS_AND_CONDITIONS, BuildConfig.LINK_PRIVACY_POLICY)
        }
    }

    internal fun refresh() {
        refresh { repository.loadNotifications().toCompletable() }
    }

    internal fun notificationClicked(notification: NotificationItem) {
        if (!notification.isRead) {
            disposables += repository.readNotification(notification.id)
                .subscribe({
                    navigateToDestination(notification.deepLink)
                }, { error ->
                    Timber.e(error)
                    navigateToDestination(notification.deepLink)
                })
        } else {
            navigateToDestination(notification.deepLink)
        }
    }

    internal fun archiveNotifications(ids: List<String>) {
        disposables += repository.archiveNotifications(ids).subscribe({
            // nothing
        }, { error ->
            Timber.e(error)
        })
    }

    private fun observeNotifications() {
        disposables += repository.observeNotifications()
            .observeOn(schedulers.disk)
            .map(::mapToAdapterItems)
            .observeOn(schedulers.main)
            .subscribe({ items ->
                view?.showNoNotifications(items.isEmpty())
                view?.showNotifications(items)
            }, { error ->
                view?.showNoNotifications(true)
                Timber.e(error)
            })
    }

    private fun mapToAdapterItems(notifications: List<Notification>): List<InboxItem> {
        val items = mutableListOf<InboxItem>()

        if (notifications.isEmpty()) return items

        val eventNotifications = notifications.filter { it.event != null }
        val eventNotificationGroups = eventNotifications.groupBy { it.eventId }
        val generalNotifications = notifications.subtract(eventNotifications)

        for ((_, groupedNotifications) in eventNotificationGroups) {
            val notificationItems = mutableListOf<NotificationItem>()

            groupedNotifications.forEach {
                if (it.type != null) {
                    val timestamp = notificationTimestamp(it.createDate!!)
                    when {
                        it.type!!.isList() -> {
                            notificationItems.add(ListItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                        it.type!!.isOnSale() -> {
                            notificationItems.add(OnSaleItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                        it.type!!.isSecured() -> {
                            notificationItems.add(SecuredItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                        it.type!!.isActionRequired() -> {
                            notificationItems.add(ActionRequiredItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                        it.type!!.isAssignment() -> {
                            notificationItems.add(AssignmentItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                        it.type!!.isReminder() -> {
                            notificationItems.add(ReminderItem(it.id, it.read!!, it.deepLink, timestamp,
                                it.createDate!!, it.body!!))
                        }
                    }
                }
            }

            if (notificationItems.isNotEmpty()) {
                notificationItems.sortByDescending { it.createDate }

                val unread = notificationItems.filter { !it.isRead }

                val lastUpdated = notificationItems.first()
                notificationItems.remove(lastUpdated)

                val eventNotification = groupedNotifications.first()
                val event = eventNotification.event!!

                items.add(EventItem(eventNotification.eventId!!, event.name!!,
                    DATE_FORMAT.format(event.date), event.defaultMedia?.url,
                    lastUpdated, notificationItems, unread.isNotEmpty(), lastUpdated.createDate))
            }
        }

        generalNotifications.forEach {
            if (it.type != null && it.type!!.isGeneral()) {
                items.add(GeneralItem(it.id, it.read!!, it.deepLink,
                    notificationTimestamp(it.createDate!!), it.createDate!!, it.body!!))
            }
        }

        items.sortByDescending { it.createDate }

        return items
    }

    private fun notificationTimestamp(date: Date): String {
        val current = System.currentTimeMillis()
        val time = date.time

        val diff = current - time

        val minute: Long = 60 * 1000
        val hour: Long = 60 * minute
        val day: Long = 24 * hour
        val week: Long = 6 * day

        return when {
            diff > (week) -> DATE_FORMAT.format(date)
            diff in (day + 1)..week -> "${TimeUnit.MILLISECONDS.toDays(diff)}d"
            diff in (hour + 1)..day -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
            diff in (minute + 1)..hour -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
            else -> "1m"
        }
    }

    private fun navigateToDestination(deepLink: String?) {
        if (deepLink != null && deepLink.isNotBlank()) {
            view?.showNotificationDestination(deepLink)
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("M/d", Locale.getDefault())
    }
}