package com.enovlab.yoop.ui.main.discover

import com.enovlab.yoop.ui.main.discover.adapter.DiscoverItem
import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/8/2018.
 */
interface DiscoverView : StateView {
    fun showEvents(events: List<DiscoverItem>)
    fun showFilterActive(active: Boolean)
    fun showAllEvents()
    fun showCityName(locationName: String)
    fun showSaleState(active: Boolean)
    fun showFirstAccessTickets()
    fun showOnSaleTickets()
    fun showEmptyEvents(active: Boolean)
}