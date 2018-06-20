package com.enovlab.yoop.ui.filter.search.adapter

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.ui.base.list.ListItem
import com.enovlab.yoop.ui.base.list.LoadingAdapter
import com.enovlab.yoop.ui.base.list.LoadingViewHolder

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchCityAdapter : LoadingAdapter<City>() {

    var listener: ((City) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            ListItem.Type.LOADING.ordinal -> LoadingViewHolder(parent)
            else -> SearchCityViewHolder(parent, listener)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is SearchCityViewHolder -> holder.bind(getItem(position))
            is LoadingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingType(position) -> ListItem.Type.LOADING.ordinal
            else -> ListItem.Type.SEARCH.ordinal
        }
    }

    override fun createDiffCallback() = object : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City?, newItem: City?): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: City?, newItem: City?): Boolean {
            return oldItem == newItem
        }
    }

    override fun onInserted(position: Int, count: Int) {
        when {
            isLoading -> notifyDataSetChanged()
            else -> super.onInserted(position, count)
        }
    }
}