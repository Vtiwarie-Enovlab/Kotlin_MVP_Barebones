package com.enovlab.yoop.ui.settings.notifications.adapter

import com.enovlab.yoop.api.response.settings.NotificationSettings

sealed class NotificationItem {

    data class SettingsNotificationItem(val type: NotificationSettings.Type,
                                        var enabled: Boolean,
                                        val group: NotificationSettings.Group,
                                        val description: String) : NotificationItem()

    data class HeaderNotificationItem(val title: String) : NotificationItem()
}