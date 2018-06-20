package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.notification

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.*

class NotificationAdapter : BaseAdapter<InboxItem.NotificationItem, NotificationViewHolder>() {

    var listener: ((NotificationItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback()= object : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(oldItem: NotificationItem?, newItem: NotificationItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotificationItem?, newItem: NotificationItem?): Boolean {
            return oldItem == newItem
        }
    }
}