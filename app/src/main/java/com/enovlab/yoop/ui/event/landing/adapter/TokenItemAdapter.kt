package com.enovlab.yoop.ui.event.landing.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter

class TokenItemAdapter : BaseAdapter<TokenItem, TokenItemViewHolder>() {

    var listener: ((TokenItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenItemViewHolder {
        return TokenItemViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: TokenItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<TokenItem>() {
        override fun areItemsTheSame(oldItem: TokenItem?, newItem: TokenItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TokenItem?, newItem: TokenItem?): Boolean {
            return oldItem == newItem
        }
    }
}