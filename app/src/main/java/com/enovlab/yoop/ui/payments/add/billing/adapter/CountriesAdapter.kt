package com.enovlab.yoop.ui.payments.add.billing.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.data.entity.Country
import com.enovlab.yoop.ui.base.list.BaseAdapter
import com.enovlab.yoop.ui.base.list.BaseViewHolder

/**
 * Created by mtosk on 3/8/2018.
 */
class CountriesAdapter : BaseAdapter<Country, BaseViewHolder>() {

    var listener: ((Country) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CountriesViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        (holder as CountriesViewHolder).bind(getItem(position))
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }
}