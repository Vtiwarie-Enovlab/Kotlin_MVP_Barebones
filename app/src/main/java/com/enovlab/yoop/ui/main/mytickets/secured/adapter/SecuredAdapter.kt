package com.enovlab.yoop.ui.main.mytickets.secured.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder
import com.enovlab.yoop.ui.event.landing.adapter.TokenItem
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens.SecuredTokenItem
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens.UnVerifiedSecuredItem
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder.PendingViewHolder
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder.SecuredTicketsViewHolder
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.holder.UnVerifiedViewHolder


class SecuredAdapter : LoadingAdapter<SecuredTokens>() {

    var createIdListener: (() -> Unit)? = null
    var tokenClickedListener: ((eventId: String, tokenItem: TokenItem) -> Unit)? = null
    var eventListener: ((eventId: String) -> Unit)? = null
    var pendingListener: ((eventId: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_UNVERIFIED -> UnVerifiedViewHolder(parent, createIdListener)
            VIEW_TYPE_HAS_TICKETS -> SecuredTicketsViewHolder(parent, tokenClickedListener, eventListener)
            VIEW_TYPE_PENDING -> PendingViewHolder(parent, pendingListener)
            else -> LoadingViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is UnVerifiedViewHolder -> holder.bind()
            is SecuredTicketsViewHolder -> holder.bind(getItem(position) as SecuredTokenItem)
            is PendingViewHolder -> holder.bind(getItem(position) as SecuredTokens.PendingTokenItem)
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            getItem(position) is UnVerifiedSecuredItem -> VIEW_TYPE_UNVERIFIED
            getItem(position) is SecuredTokenItem -> VIEW_TYPE_HAS_TICKETS
            getItem(position) is SecuredTokens.PendingTokenItem -> VIEW_TYPE_PENDING
            else -> throw IllegalStateException("No type defined")
        }
    }

    override fun getItemId(position: Int) = when {
        size() > 0 && position < size() -> getItem(position).eventId.toLong()
        else -> super.getItemId(position)
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<SecuredTokens>() {
        override fun areItemsTheSame(oldItem: SecuredTokens, newItem: SecuredTokens): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: SecuredTokens, newItem: SecuredTokens): Boolean {
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
        private const val VIEW_TYPE_UNVERIFIED = 200
        private const val VIEW_TYPE_HAS_TICKETS = 201
        private const val VIEW_TYPE_PENDING = 202
    }
}