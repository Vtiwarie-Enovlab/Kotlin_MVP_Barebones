package com.enovlab.yoop.ui.main.mytickets

import com.enovlab.yoop.ui.base.state.StateView

interface MyTicketsView : StateView {
    fun showNotificationsCountActive(active: Boolean)
    fun showNotificationsCount(count: String)
}