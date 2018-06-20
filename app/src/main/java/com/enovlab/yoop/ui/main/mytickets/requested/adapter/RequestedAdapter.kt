package com.enovlab.yoop.ui.main.mytickets.requested.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem.*
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.holder.*

class RequestedAdapter : LoadingAdapter<RequestedItem>() {

    var listener: ((RequestedItem) -> Unit)? = null
    var editListener: ((EditData) -> Unit)? = null
    var detailsListener: ((DetailsData) -> Unit)? = null
    var archiveListener: ((List<String>) -> Unit)? = null
    var fixListener: ((EditData) -> Unit)? = null
    var claimListener: ((EditData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            VIEW_TYPE_ACTIVE_LIST -> RequestedActiveListViewHolder(parent, listener, editListener)
            VIEW_TYPE_ACTIVE_ON_SALE -> RequestedActiveOnSaleViewHolder(parent, listener, editListener)
            VIEW_TYPE_TRY_LIST -> RequestedTryListViewHolder(parent, listener, detailsListener, archiveListener)
            VIEW_TYPE_TRY_ON_SALE -> RequestedTryOnSaleViewHolder(parent, listener, detailsListener, archiveListener)
            VIEW_TYPE_OPENS_LIST -> RequestedOpensListViewHolder(parent, listener, archiveListener)
            VIEW_TYPE_OPENS_ON_SALE -> RequestedOpensOnSaleViewHolder(parent, listener, archiveListener)
            VIEW_TYPE_CLOSED -> RequestedClosedViewHolder(parent, listener, archiveListener)
            VIEW_TYPE_PENDING_LIST -> RequestedPendingListViewHolder(parent, listener)
            VIEW_TYPE_PENDING_ON_SALE -> RequestedPendingOnSaleViewHolder(parent, listener)
            VIEW_TYPE_FIX_LIST -> RequestedFixListViewHolder(parent, listener, fixListener, claimListener)
            VIEW_TYPE_FIX_ON_SALE -> RequestedFixOnSaleViewHolder(parent, listener, fixListener, claimListener)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is RequestedActiveListViewHolder -> holder.bind(getItem(position) as ActiveListItem)
            is RequestedActiveOnSaleViewHolder -> holder.bind(getItem(position) as ActiveOnSaleItem)
            is RequestedTryListViewHolder -> holder.bind(getItem(position) as TryListItem)
            is RequestedTryOnSaleViewHolder -> holder.bind(getItem(position) as TryOnSaleItem)
            is RequestedOpensListViewHolder -> holder.bind(getItem(position) as RequestedItem.OpensListItem)
            is RequestedOpensOnSaleViewHolder -> holder.bind(getItem(position) as RequestedItem.OpensOnSaleItem)
            is RequestedClosedViewHolder -> holder.bind(getItem(position) as RequestedItem.ClosedItem)
            is RequestedPendingListViewHolder -> holder.bind(getItem(position) as RequestedItem.PendingListItem)
            is RequestedPendingOnSaleViewHolder -> holder.bind(getItem(position) as RequestedItem.PendingOnSaleItem)
            is RequestedFixListViewHolder -> holder.bind(getItem(position) as RequestedItem.ActionRequiredListItem)
            is RequestedFixOnSaleViewHolder -> holder.bind(getItem(position) as RequestedItem.ActionRequiredOnSaleItem)
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            getItem(position) is ActiveListItem -> VIEW_TYPE_ACTIVE_LIST
            getItem(position) is ActiveOnSaleItem -> VIEW_TYPE_ACTIVE_ON_SALE
            getItem(position) is TryListItem -> VIEW_TYPE_TRY_LIST
            getItem(position) is TryOnSaleItem -> VIEW_TYPE_TRY_ON_SALE
            getItem(position) is OpensListItem -> VIEW_TYPE_OPENS_LIST
            getItem(position) is OpensOnSaleItem -> VIEW_TYPE_OPENS_ON_SALE
            getItem(position) is ClosedItem -> VIEW_TYPE_CLOSED
            getItem(position) is PendingListItem -> VIEW_TYPE_PENDING_LIST
            getItem(position) is PendingOnSaleItem -> VIEW_TYPE_PENDING_ON_SALE
            getItem(position) is ActionRequiredListItem -> VIEW_TYPE_FIX_LIST
            getItem(position) is ActionRequiredOnSaleItem -> VIEW_TYPE_FIX_ON_SALE
            else -> throw IllegalStateException("No type defined")
        }
    }

    override fun getItemId(position: Int) = when {
        size() > 0 && position < size() -> getItem(position).id.toLong()
        else -> super.getItemId(position)
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<RequestedItem>() {
        override fun areItemsTheSame(oldItem: RequestedItem?, newItem: RequestedItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RequestedItem?, newItem: RequestedItem?): Boolean {
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
        private const val VIEW_TYPE_ACTIVE_LIST = 99
        private const val VIEW_TYPE_ACTIVE_ON_SALE = 98
        private const val VIEW_TYPE_TRY_LIST = 97
        private const val VIEW_TYPE_TRY_ON_SALE = 96
        private const val VIEW_TYPE_OPENS_LIST = 95
        private const val VIEW_TYPE_OPENS_ON_SALE = 94
        private const val VIEW_TYPE_CLOSED = 93
        private const val VIEW_TYPE_PENDING_LIST = 92
        private const val VIEW_TYPE_PENDING_ON_SALE = 91
        private const val VIEW_TYPE_FIX_LIST = 90
        private const val VIEW_TYPE_FIX_ON_SALE = 89
    }

    data class DetailsData(val id: String, val marketplaceType: MarketplaceType)

    data class EditData(val id: String, val marketplaceType: MarketplaceType, val offerGroupId: String)
}