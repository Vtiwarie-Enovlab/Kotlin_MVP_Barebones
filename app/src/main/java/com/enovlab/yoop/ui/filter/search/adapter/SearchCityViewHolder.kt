package com.enovlab.yoop.ui.filter.search.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_search_city_list.*

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchCityViewHolder(parent: ViewGroup, val listener: ((City) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_search_city_list, parent)) {

    fun bind(city: City) {
        name.text = "${city.name}, ${city.regionAbbreviation}"

        if (listener != null)
            containerView?.setOnClickListener { listener.invoke(city) }
    }
}