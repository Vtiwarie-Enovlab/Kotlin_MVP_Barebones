package com.enovlab.yoop.ui.transaction.details.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter

/**
 * Created by mtosk on 3/8/2018.
 */
class OfferGroupAdapter : BaseAdapter<OfferGroupItem, OfferGroupViewHolder>() {

    var listener: ((OfferGroupItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferGroupViewHolder {
        return OfferGroupViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: OfferGroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<OfferGroupItem>() {
        override fun areItemsTheSame(oldItem: OfferGroupItem, newItem: OfferGroupItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OfferGroupItem, newItem: OfferGroupItem): Boolean {
            return oldItem == newItem
        }
    }
}