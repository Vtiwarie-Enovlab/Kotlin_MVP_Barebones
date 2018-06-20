package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.notification

import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem.*
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_inbox_notification.*

class NotificationViewHolder(parent: ViewGroup,
                             val listenerNotification: ((NotificationItem) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_inbox_notification, parent)) {

    fun bind(item: NotificationItem) {
        when (item) {
            is ListItem -> {
                notification_icon.setImageResource(R.drawable.ic_list_white_26dp)
                notification_message.text = item.message
            }
            is OnSaleItem -> {
                notification_icon.setImageResource(R.drawable.ic_onsale_white_26dp)
                notification_message.text = item.message
            }
            is SecuredItem -> {
                notification_icon.setImageResource(R.drawable.ic_ticket_white_26dp)
                notification_message.text = item.message
            }
            is AssignmentItem -> {
                notification_icon.setImageResource(R.drawable.ic_ticket_white_26dp)
                notification_message.text = item.message
            }
            is ActionRequiredItem -> {
                notification_icon.setImageResource(R.drawable.ic_alert_white_26dp)
                notification_message.text = item.message
            }
            is ReminderItem -> {
                notification_icon.setImageResource(R.drawable.ic_reminder_white_26dp)
                notification_message.text = item.message
            }
        }

        notification_date.text = item.timestamp

        val color = ContextCompat.getColor(itemView.context, when {
            item.isRead -> R.color.color_white_alpha_50
            else -> R.color.color_white
        })
        notification_icon.imageTintList = ColorStateList.valueOf(color)
        notification_message.setTextColor(color)
        notification_date.setTextColor(color)
        notification_unread.isInvisible = item.isRead

        itemView.setOnClickListener {
            listenerNotification?.invoke(item)
        }
    }
}