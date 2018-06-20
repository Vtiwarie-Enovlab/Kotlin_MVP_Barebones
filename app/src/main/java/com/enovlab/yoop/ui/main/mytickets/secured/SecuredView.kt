package com.enovlab.yoop.ui.main.mytickets.secured

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens

interface SecuredView : StateView {
    fun showEmptyEvents(show: Boolean)
    fun showSecuredItems(items: List<SecuredTokens>)
    fun showUnverifiedBanner(show: Boolean)
    fun showTicketDetails(id: String, ticketId: String)
    fun showProfileCapture()
    fun showProfileIntro()
    fun showProfilePic(url: String?)
}