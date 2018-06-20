package com.enovlab.yoop.ui.transaction.count.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter

/**
 * Created by mtosk on 3/8/2018.
 */
class CountPickerAdapter : BaseAdapter<CountItem, CountPickerViewHolder>() {

    var listener: ((CountItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountPickerViewHolder {
        return CountPickerViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: CountPickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<CountItem>() {
        override fun areItemsTheSame(oldItem: CountItem, newItem: CountItem): Boolean {
            return oldItem.count == newItem.count
        }

        override fun areContentsTheSame(oldItem: CountItem, newItem: CountItem): Boolean {
            return oldItem.selected == newItem.selected
        }
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        notifyDataSetChanged()
    }
}