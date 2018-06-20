package com.enovlab.yoop.ui.search.event.adapter

import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.api.response.EventSearch
import com.enovlab.yoop.ui.base.list.BaseViewHolder
import com.enovlab.yoop.utils.ext.inflateView
import kotlinx.android.synthetic.main.item_search_event_list.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchEventViewHolder(parent: ViewGroup, val listener: ((EventSearch) -> Unit)? = null)
    : BaseViewHolder(inflateView(R.layout.item_search_event_list, parent)) {

    fun bind(event: EventSearch) {
        name.text = event.eventName
        location.text = "${DATE_FORMAT.format(event.eventDate)} • ${event.locationName}"

        if (listener != null)
            containerView?.setOnClickListener { listener.invoke(event) }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("EEE, M/dd", Locale.getDefault())
    }
}