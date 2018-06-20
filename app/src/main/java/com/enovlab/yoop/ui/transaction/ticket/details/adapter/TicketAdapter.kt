package com.enovlab.yoop.ui.transaction.ticket.details.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder
import com.enovlab.yoop.ui.transaction.ticket.details.adapter.vh.*

class TicketAdapter : LoadingAdapter<TicketItem>() {

    var listenerTransfer: ((TicketItem) -> Unit)? = null
    var listenerCreateId: (() -> Unit)? = null
    var listenerChanges: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            VIEW_TYPE_ASSIGNED -> TicketAssignedViewHolder(parent)
            VIEW_TYPE_PENDING -> TicketPendingViewHolder(parent)
            VIEW_TYPE_UNASSIGNED -> TicketUnassignedViewHolder(parent, listenerTransfer)
            VIEW_TYPE_USER -> TicketUserViewHolder(parent, listenerCreateId)
            VIEW_TYPE_ASSIGNEE -> TicketAssigneeViewHolder(parent, listenerCreateId)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is TicketAssignedViewHolder -> holder.bind(getItem(position) as TicketItem.AssignedItem)
            is TicketPendingViewHolder -> holder.bind(getItem(position) as TicketItem.PendingItem)
            is TicketUnassignedViewHolder -> holder.bind(getItem(position) as TicketItem.UnassignedItem)
            is TicketUserViewHolder -> holder.bind(getItem(position) as TicketItem.UserItem)
            is TicketAssigneeViewHolder -> holder.bind(getItem(position) as TicketItem.AssigneeItem)
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            getItem(position) is TicketItem.AssignedItem -> VIEW_TYPE_ASSIGNED
            getItem(position) is TicketItem.PendingItem -> VIEW_TYPE_PENDING
            getItem(position) is TicketItem.UnassignedItem -> VIEW_TYPE_UNASSIGNED
            getItem(position) is TicketItem.UserItem -> VIEW_TYPE_USER
            getItem(position) is TicketItem.AssigneeItem -> VIEW_TYPE_ASSIGNEE
            else -> throw IllegalStateException("No type defined")
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<TicketItem>() {
        override fun areItemsTheSame(oldItem: TicketItem, newItem: TicketItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TicketItem, newItem: TicketItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onInserted(position: Int, count: Int) {
        when {
            isLoading -> notifyDataSetChanged()
            else -> super.onInserted(position, count)
        }
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        super.onChanged(position, count, payload)
        listenerChanges?.invoke()
    }

    companion object {
        private const val VIEW_TYPE_ASSIGNED = 31
        private const val VIEW_TYPE_PENDING = 32
        private const val VIEW_TYPE_UNASSIGNED = 33
        private const val VIEW_TYPE_USER = 34
        private const val VIEW_TYPE_ASSIGNEE = 35
    }
}