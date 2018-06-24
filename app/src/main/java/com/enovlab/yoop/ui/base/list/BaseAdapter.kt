package com.enovlab.yoop.ui.base.list

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected abstract fun createDiffCallback(): DiffUtil.ItemCallback<T>

    private val helper: AsyncListDiffer<T>

    init {
        @Suppress("LeakingThis")
        helper = AsyncListDiffer(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                this@BaseAdapter.onInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                this@BaseAdapter.onRemoved(position, count)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                this@BaseAdapter.onChanged(position, count, payload)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                this@BaseAdapter.onMoved(fromPosition, toPosition)
            }
        }, AsyncDifferConfig.Builder(createDiffCallback()).build())
    }

    protected open fun onInserted(position: Int, count: Int) {
        notifyItemRangeInserted(position, count)
    }

    protected open fun onRemoved(position: Int, count: Int) {
        notifyItemRangeRemoved(position, count)
    }

    protected open fun onChanged(position: Int, count: Int, payload: Any?) {
        notifyItemRangeChanged(position, count, payload)
    }

    protected open fun onMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Submits a new list to be diffed, and displayed.
     *
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * @param list The new list to be displayed.
     */
    fun submitList(list: List<T>) {
        when {
            list.isEmpty() -> clear()
            else -> helper.submitList(list)
        }
    }

    fun clear() {
        helper.submitList(null)
    }

    fun isEmpty() = helper.currentList.isEmpty()

    fun size() = helper.currentList.size

    fun getItem(position: Int): T {
        return helper.currentList[position]
    }

    fun getItems(): List<T> = helper.currentList

    override fun getItemCount(): Int {
        return helper.currentList.size
    }
}