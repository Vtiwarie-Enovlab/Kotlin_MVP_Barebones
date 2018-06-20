package com.enovlab.yoop.ui.filter.details

import com.enovlab.yoop.ui.base.state.StateView

/**
 * Created by mtosk on 3/14/2018.
 */
interface FilterDetailsView : StateView {
    fun showLocationName(name: String? = null)
    fun showAllLocations(active: Boolean)
    fun showSpecificLocation(active: Boolean)
    fun showAllTickets(active: Boolean)
    fun showFirstAccessTickets(active: Boolean)
    fun showOnSaleTickets(active: Boolean)
    fun showRadius(radius: Int)
}