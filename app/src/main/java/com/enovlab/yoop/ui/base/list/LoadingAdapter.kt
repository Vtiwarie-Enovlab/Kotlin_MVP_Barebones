package com.enovlab.yoop.ui.base.list

import android.support.v7.widget.RecyclerView

abstract class LoadingAdapter<E> : BaseAdapter<E, BaseViewHolder>() {

    private val loadingMoreItemPosition: Int
        get() = super.getItemCount()

    var isLoading = false
        set(value) {
            if (value != field) {
                val position = loadingMoreItemPosition
                when {
                    value -> notifyItemInserted(position)
                    else -> notifyItemRemoved(position)
                }
                field = value
            }
        }

    override fun getItemId(position: Int): Long = when (getItemViewType(position)) {
        ListItem.Type.LOADING.ordinal -> RecyclerView.NO_ID
        else -> super.getItemId(position)
    }

    override fun getItemCount(): Int = super.getItemCount() + if (isLoading) 1 else 0

    fun isLoadingType(position: Int): Boolean {
        return position >= super.getItemCount()
    }
}