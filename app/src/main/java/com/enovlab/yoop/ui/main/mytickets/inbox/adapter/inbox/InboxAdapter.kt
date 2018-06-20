package com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem
import com.enovlab.yoop.ui.main.mytickets.inbox.InboxItem.NotificationItem.GeneralItem

class InboxAdapter : LoadingAdapter<InboxItem>() {

    var listenerEvent: ((InboxItem.EventItem) -> Unit)? = null
    var listenerNotification: ((InboxItem.NotificationItem) -> Unit)? = null
    var listenerArchive: ((List<String>) -> Unit)? = null

    private val expandedItems = mutableMapOf<String, Boolean>()
    private val listenerExpand: (String, Boolean) -> Unit = { id, expand ->
        expandedItems[id] = expand
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            VIEW_TYPE_EVENT -> InboxEventViewHolder(parent, listenerEvent, listenerNotification, listenerArchive, listenerExpand)
            VIEW_TYPE_GENERAL -> InboxGeneralViewHolder(parent, listenerNotification, listenerArchive)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is InboxEventViewHolder -> {
                val item = getItem(position) as InboxItem.EventItem
                holder.bind(item, expandedItems[item.id])
            }
            is InboxGeneralViewHolder -> {
                val item = getItem(position) as GeneralItem
                holder.bind(item)
            }
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            getItem(position) is InboxItem.EventItem -> VIEW_TYPE_EVENT
            getItem(position) is InboxItem.NotificationItem -> VIEW_TYPE_GENERAL
            else -> throw IllegalStateException("No type defined")
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<InboxItem>() {
        override fun areItemsTheSame(oldItem: InboxItem?, newItem: InboxItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: InboxItem?, newItem: InboxItem?): Boolean {
            return oldItem == newItem
        }
    }

    override fun onInserted(position: Int, count: Int) {
        when {
            isLoading -> notifyDataSetChanged()
            else -> super.onInserted(position, count)
        }
    }

    companion object {
        private const val VIEW_TYPE_EVENT = 41
        private const val VIEW_TYPE_GENERAL = 42
    }
}