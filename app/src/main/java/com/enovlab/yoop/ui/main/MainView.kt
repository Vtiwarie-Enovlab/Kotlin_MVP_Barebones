package com.enovlab.yoop.ui.main

import android.net.Uri
import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.main.mytickets.MyTicketsNavigation

/**
 * Created by mtosk on 3/7/2018.
 */
interface MainView : StateView {
    fun showAuthLogin()
    fun showAuthSignupVerificationExpired(email: String)
    fun showAuthResetPasswordVerificationExpired(email: String)
    fun showAuthResetPassword(token: String)
    fun showMyTickets(navigation: MyTicketsNavigation)
    fun showTokenAssignment(uri: Uri)
    fun showEventLanding(eventId: String)
    fun showTransactionDetails(eventId: String, marketplaceType: String)
    fun showTransactionEdit(eventId: String, marketplaceType: String, offerGroupId: String)
    fun showTransactionReviewFix(eventId: String, marketplaceType: String, offerGroupId: String)
    fun showTransactionReviewClaim(eventId: String, marketplaceType: String, offerGroupId: String)
    fun showNotificationsBadge(active: Boolean)
}