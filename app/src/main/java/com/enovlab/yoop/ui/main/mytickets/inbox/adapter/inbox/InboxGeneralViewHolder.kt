package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox

import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_inbox_general.*
import kotlinx.android.synthetic.main.layout_inbox_archive.*

class InboxGeneralViewHolder(parent: ViewGroup,
                             val listenerNotification: ((NotificationItem) -> Unit)? = null,
                             var listenerArchive: ((List<String>) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_inbox_general, parent)) {

    fun bind(item: NotificationItem.GeneralItem) {
        notification_message.text = item.message

        notification_date.text = item.timestamp

        val color = ContextCompat.getColor(itemView.context, when {
            item.isRead -> R.color.color_white_alpha_50
            else -> R.color.color_white
        })
        notification_icon.imageTintList = ColorStateList.valueOf(color)
        notification_message.setTextColor(color)
        notification_date.setTextColor(color)
        notification_unread.isInvisible = item.isRead

        container_inbox.setOnClickListener { listenerNotification?.invoke(item) }

        inbox_archive_count.setText(R.string.my_ticket_inbox_archive_single)
        inbox_archive.setOnClickListener {
            listenerArchive?.invoke(listOf(item.id))
        }
    }
}