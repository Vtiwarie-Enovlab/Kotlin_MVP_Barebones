package com.enovlab.yoop.ui.search.event

import com.enovlab.yoop.api.response.EventSearch
import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/13/2018.
 */
interface SearchEventView : StateView {
    fun showEventsSearch(events: List<EventSearch>)
    fun showNoResults()
}