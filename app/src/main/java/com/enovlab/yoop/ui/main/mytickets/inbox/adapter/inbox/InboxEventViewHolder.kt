package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox

import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SimpleItemAnimator
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.EventItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem.*
import com.enovlab.yoop.ui.main.mytickets.inbox.adapter.notification.NotificationAdapter
import com.enovlab.yoop.utils.ext.inflateView
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.item_inbox_event.*
import kotlinx.android.synthetic.main.item_inbox_notification.*
import kotlinx.android.synthetic.main.layout_inbox_archive.*

class InboxEventViewHolder(parent: ViewGroup,
                           val listenerEvent: ((EventItem) -> Unit)? = null,
                           val listenerNotification: ((NotificationItem) -> Unit)? = null,
                           var listenerArchive: ((List<String>) -> Unit)? = null,
                           val listenerExpand: (String, Boolean) -> Unit)
    : BaseViewHolder(inflateView(R.layout.item_inbox_event, parent)) {

    fun bind(item: EventItem, expanded: Boolean?) {
        var isExpanded = expanded == true

        inbox_performer_picture.loadImage(item.performerPicture)
        inbox_event_name.text = item.eventName
        inbox_event_date.text = ", ${item.eventDate}"
        container_inbox.setOnClickListener { listenerEvent?.invoke(item) }

        bindLastNotification(item.lastUpdatedNotification)
        inbox_latest_notification.setOnClickListener {
            listenerNotification?.invoke(item.lastUpdatedNotification)
        }

        if (item.notifications.isNotEmpty()) {
            inbox_more.setText(when {
                isExpanded -> R.string.my_ticket_inbox_item_less
                else -> R.string.my_ticket_inbox_item_more
            })
            inbox_more.isVisible = true
            inbox_unread_notifications.isInvisible = !item.containsUnread
            inbox_notifications.isVisible = isExpanded

            inbox_more.setOnClickListener {
                isExpanded = !isExpanded

                listenerExpand(item.id, isExpanded)

                inbox_notifications.isVisible = isExpanded
                inbox_more.setText(when {
                    isExpanded -> R.string.my_ticket_inbox_item_less
                    else -> R.string.my_ticket_inbox_item_more
                })
                swipe_layout.isSwipeEnabled = !isExpanded
            }

            val adapter = NotificationAdapter()
            adapter.listener = { listenerNotification?.invoke(it) }
            inbox_notifications.adapter = adapter
            if (inbox_notifications.itemDecorationCount == 0) {
                inbox_notifications.addItemDecoration(InboxItemDecoration(itemView.resources.getDimensionPixelSize(R.dimen.margin_sub_large)))
            }
            (inbox_notifications.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter.submitList(item.notifications)

            inbox_archive_count.text = itemView.resources.getString(R.string.my_ticket_inbox_archive, (item.notifications.size + 1))
        } else {
            inbox_unread_notifications.isVisible = false
            inbox_more.isVisible = false
            inbox_notifications.isVisible = false

            inbox_archive_count.setText(R.string.my_ticket_inbox_archive_single)
        }

        swipe_layout.isSwipeEnabled = !isExpanded
        inbox_archive.setOnClickListener {
            listenerArchive?.invoke(notificationIds(item))
        }
    }

    private fun bindLastNotification(item: NotificationItem) {
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
    }

    private fun notificationIds(item: EventItem): List<String> {
        return mutableListOf(item.lastUpdatedNotification.id).apply {
            if (item.notifications.isNotEmpty())
                addAll(item.notifications.map { it.id })
        }
    }
}