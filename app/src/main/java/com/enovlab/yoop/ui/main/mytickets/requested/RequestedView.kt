package com.enovlab.yoop.ui.main.mytickets.requested

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem

interface RequestedView : StateView {
    fun showFilters(active: Boolean)
    fun showAllTickets(active: Boolean)
    fun showYoopListTickets(active: Boolean)
    fun showOnSaleTickets(active: Boolean)
    fun showRequestedItems(items: List<RequestedItem>)
    fun showEmptyEvents(active: Boolean)
}