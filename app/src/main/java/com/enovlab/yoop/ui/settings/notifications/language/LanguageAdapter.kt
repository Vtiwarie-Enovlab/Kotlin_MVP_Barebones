package com.enovlab.yoop.ui.settings.notifications.language

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.ui.base.list.BaseAdapter

class LanguageAdapter : BaseAdapter<LanguageItem, LanguageViewHolder>() {

    var listener: ((LanguageItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        return LanguageViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<LanguageItem>() {
        override fun areItemsTheSame(oldItem: LanguageItem?, newItem: LanguageItem?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LanguageItem?, newItem: LanguageItem?): Boolean {
            return oldItem == newItem
        }
    }
}