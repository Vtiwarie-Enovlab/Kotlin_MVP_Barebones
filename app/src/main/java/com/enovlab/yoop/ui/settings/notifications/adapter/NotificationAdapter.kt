package com.enovlab.yoop.ui.settings.notifications.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem.HeaderNotificationItem
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem.SettingsNotificationItem

class NotificationAdapter : LoadingAdapter<NotificationItem>() {

    var listener: ((SettingsNotificationItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_SETTINGS -> NotificationViewHolder(parent, listener)
            TYPE_HEADER -> NotificationHeaderViewHolder(parent)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when {
            holder is NotificationViewHolder -> holder.bind(getItem(position) as SettingsNotificationItem)
            holder is NotificationHeaderViewHolder -> holder.bind(getItem(position) as HeaderNotificationItem)
            holder is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            getItem(position) is HeaderNotificationItem -> TYPE_HEADER
            getItem(position) is SettingsNotificationItem -> TYPE_SETTINGS
            else -> super.getItemViewType(position)
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(oldItem: NotificationItem?, newItem: NotificationItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotificationItem?, newItem: NotificationItem?): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TYPE_HEADER = 187
        private const val TYPE_SETTINGS = 188
    }
}