package com.enovlab.yoop.ui.main.mytickets.inbox

import com.enovlab.yoop.ui.base.state.StateView

interface InboxView : StateView {
    fun showNotAuthorized(active: Boolean)
    fun showNoNotifications(active: Boolean)
    fun showNotifications(items: List<InboxItem>)
    fun showRefreshEnabled(enabled: Boolean)
    fun showLegalLinks(termsUrl: String, privacyUrl: String)
    fun showNotificationDestination(deepLink: String)
}