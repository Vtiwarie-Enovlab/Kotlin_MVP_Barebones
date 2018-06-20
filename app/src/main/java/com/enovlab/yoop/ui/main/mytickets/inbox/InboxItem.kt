package com.enovlab.yoop.ui.main.mytickets.inbox

import java.util.*

sealed class InboxItem(open val createDate: Date) {

    data class EventItem(
        val id: String,
        val eventName: String,
        val eventDate: String,
        val performerPicture: String?,
        val lastUpdatedNotification: NotificationItem,
        val notifications: List<NotificationItem>,
        val containsUnread: Boolean,
        override val createDate: Date) : InboxItem(createDate)

    sealed class NotificationItem(
        open val id: String,
        open val isRead: Boolean,
        open val deepLink: String?,
        open val timestamp: String,
        override val createDate: Date) : InboxItem(createDate) {

        data class ListItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class OnSaleItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class SecuredItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class AssignmentItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class ActionRequiredItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class ReminderItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)

        data class GeneralItem(
            override val id: String,
            override val isRead: Boolean,
            override val deepLink: String?,
            override val timestamp: String,
            override val createDate: Date,
            val message: String
        ) : NotificationItem(id, isRead, deepLink, timestamp, createDate)
    }
}