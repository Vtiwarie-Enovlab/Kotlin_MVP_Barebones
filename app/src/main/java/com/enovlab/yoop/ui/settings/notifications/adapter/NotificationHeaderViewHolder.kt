package com.enovlab.yoop.ui.settings.notifications.adapter

import android.view.ViewGroup
import android.widget.TextView
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView

class NotificationHeaderViewHolder(parent: ViewGroup)
    : BaseViewHolder(inflateView(R.layout.item_settings_notification_header, parent)) {

    fun bind(item: NotificationItem.HeaderNotificationItem) {
        (itemView as TextView).text = item.title
    }
}