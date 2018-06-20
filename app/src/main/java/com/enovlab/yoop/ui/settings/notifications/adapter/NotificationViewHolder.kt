package com.enovlab.yoop.ui.settings.notifications.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem.SettingsNotificationItem
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_settings_notification.*

class NotificationViewHolder(parent: ViewGroup, val listener: ((SettingsNotificationItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_settings_notification, parent)) {

    fun bind(item: SettingsNotificationItem) {
        settings_notification_enabled.isChecked = item.enabled
        settings_notification_title.text = item.description

        if (listener != null) {
            settings_notification_enabled.setOnCheckedChangeListener { _, isChecked ->
                item.enabled = isChecked
                listener.invoke(item)
            }
        }
    }
}