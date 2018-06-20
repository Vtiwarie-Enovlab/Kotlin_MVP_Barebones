package com.enovlab.yoop.ui.main

import android.app.ActivityOptions
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.manager.DeepLinkManager
import com.enovlab.yoop.data.manager.DeepLinkManager.Destination.*
import com.enovlab.yoop.ui.auth.AuthActivity
import com.enovlab.yoop.ui.auth.AuthNavigator
import com.enovlab.yoop.ui.base.state.StateActivity
import com.enovlab.yoop.ui.event.EventActivity
import com.enovlab.yoop.ui.filter.FilterActivity
import com.enovlab.yoop.ui.main.discover.DiscoverFragment
import com.enovlab.yoop.ui.main.mytickets.MyTicketsFragment
import com.enovlab.yoop.ui.main.mytickets.MyTicketsNavigation
import com.enovlab.yoop.ui.main.profile.ProfileFragment
import com.enovlab.yoop.ui.payments.PaymentsActivity
import com.enovlab.yoop.ui.profile.ProfileEditActivity
import com.enovlab.yoop.ui.profile.ProfileEditNavigator
import com.enovlab.yoop.ui.search.SearchActivity
import com.enovlab.yoop.ui.settings.SettingsActivity
import com.enovlab.yoop.ui.settings.SettingsNavigator
import com.enovlab.yoop.ui.transaction.TransactionActivity
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.utils.ext.addBadge
import com.enovlab.yoop.utils.ext.check
import com.enovlab.yoop.utils.ext.createActivityOptions
import com.enovlab.yoop.utils.ext.isBadgeVisible
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : StateActivity<MainView, MainViewModel, MainNavigator>(), MainView {
    override val navigatorClass = MainNavigator::class.java
    override val vmClass = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(MainViewModel::class.java)

        main_navigation.setOnNavigationItemSelectedListener { item ->
            if (!item.isChecked) {
                when (item.itemId) {
                    R.id.action_discover -> navigateToDiscover()
                    R.id.action_my_tickets -> navigateToMyTickets(MyTicketsNavigation.REQUESTED)
                    R.id.action_account -> navigateToAccount()
                }
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener false
        }
        main_navigation.addBadge(1, R.layout.layout_main_notifications_badge)

        onNewIntent(intent)
        navigateToDiscover()
    }

    override fun setupNavigation(navigator: MainNavigator) {
        navigator.navigateToDiscover.observeNavigation { navigateToDiscover() }
        navigator.navigateToMyTickets.observeNavigation { navigateToMyTickets(it) }
        navigator.navigateToProfile.observeNavigation { navigateToAccount() }
        navigator.navigateToEventLanding.observeNavigation { navigateToEventLanding(it) }
        navigator.navigateToFilter.observeNavigation { navigateToFilter() }
        navigator.navigateToSearchEvents.observeNavigation { navigateToSearchEvents() }
        navigator.navigateToTransactionDetails.observeNavigation {
            navigateToTransactionDetails(it.first, it.second)
        }
        navigator.navigateToTransactionEdit.observeNavigation {
            navigateToTransaction(it.first, it.second, it.third, navigation = TransactionNavigator.Navigation.EDIT)
        }
        navigator.navigateToTransactionFixPayment.observeNavigation {
            navigateToTransaction(it.first, it.second, it.third, TransactionNavigator.Navigation.REVIEW_FIX_PAYMENT)
        }
        navigator.navigateToTransactionClaimTickets.observeNavigation {
            navigateToTransaction(it.first, it.second, it.third, TransactionNavigator.Navigation.REVIEW_CLAIM_TICKETS)
        }
        navigator.navigateToAuthLogin.observeNavigation { navigateToAuthLogin(AuthNavigator.Navigation.LOGIN) }
        navigator.navigateToAuthSignup.observeNavigation { navigateToAuthLogin(AuthNavigator.Navigation.SIGNUP) }
        navigator.navigateToProfileDetails.observeNavigation(::navigateToProfileDetails)
        navigator.navigateToProfileCapture.observeNavigation(::navigateToProfileCapture)
        navigator.navigateToProfileCaptureIntro.observeNavigation(::navigateToProfileCaptureIntro)
        navigator.navigateToManagePayments.observeNavigation { navigateToPayments() }
        navigator.navigateToNotifications.observeNavigation { navigateToSettings(SettingsNavigator.Navigation.NOTIFICATIONS) }
        navigator.navigateToSupport.observeNavigation { navigateToSettings(SettingsNavigator.Navigation.SUPPORT) }
        navigator.navigateToAbout.observeNavigation { navigateToSettings(SettingsNavigator.Navigation.ABOUT) }
        navigator.navigateToTicketDetails.observeNavigation { navigateToTicketDetails(it.first, it.second) }
        navigator.navigateToWebUrl.observeNavigation(::navigateToWebUrl)
        navigator.navigateThroughDeepLink.observeNavigation { navigateThroughDeepLink(it.toUri()) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.data != null) navigateThroughDeepLink(intent.data)
    }

    override fun showActionIndicator(active: Boolean) {
        deep_link_progress.isVisible = active
    }

    override fun showNotificationsBadge(active: Boolean) {
        main_navigation.isBadgeVisible(1, active)
    }

    private fun navigateThroughDeepLink(uri: Uri?) {
        val destination = DeepLinkManager.destination(uri)
        when (destination) {
            is SignupVerification -> viewModel.verifySignupEmail(destination.token)
            is ResetPasswordVerification -> viewModel.verifyResetPasswordEmail(destination.token)
            is Discover -> navigateToDiscover()
            is EventLanding -> navigateToEventLanding(destination.eventId)
            is TransactionDetails -> viewModel.checkTransactionDetails(destination.eventId,
                destination.marketplaceId)
            is TransactionEdit -> viewModel.checkTransactionEdit(destination.eventId,
                destination.marketplaceId, destination.offerGroupId)
            is TransactionReview -> viewModel.checkTransactionReview(destination.eventId,
                destination.marketplaceId, destination.offerGroupId, destination.offerId)
            is TicketDetails -> navigateToTicketDetails(destination.eventId)
            is TokenAssignment -> {
                viewModel.saveTokenAssignmentDeepLink(uri!!.toString())
                navigateToTicketAssignment(destination.eventId, destination.assignmentToken, destination.email)
            }
            is Secured -> navigateToMyTickets(MyTicketsNavigation.SECURED)
        }
    }

    private fun navigateToDiscover() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, DiscoverFragment.newInstance())
            .commit()
        main_navigation.check(R.id.action_discover)
    }

    private fun navigateToMyTickets(navigation: MyTicketsNavigation) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, MyTicketsFragment.newInstance(navigation))
            .commit()
        main_navigation.check(R.id.action_my_tickets)
    }

    private fun navigateToAccount() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, ProfileFragment.newInstance())
            .commit()
        main_navigation.check(R.id.action_account)
    }

    override fun showAuthLogin() {
        navigateToAuthLogin(AuthNavigator.Navigation.LOGIN)
    }

    override fun showAuthResetPasswordVerificationExpired(email: String) {
        navigateToAuthVerificationExpired(email, AuthNavigator.Navigation.RESET_PASSWORD_VERIFICATION_EXPIRED)
    }

    override fun showAuthSignupVerificationExpired(email: String) {
        navigateToAuthVerificationExpired(email, AuthNavigator.Navigation.SIGNUP_VERIFICATION_EXPIRED)
    }

    override fun showAuthResetPassword(token: String) {
        navigateToAuthResetPassword(token, AuthNavigator.Navigation.RESET_PASSWORD)
    }

    override fun showTokenAssignment(uri: Uri) {
        navigateThroughDeepLink(uri)
    }

    override fun showMyTickets(navigation: MyTicketsNavigation) {
        navigateToMyTickets(navigation)
    }

    override fun showEventLanding(eventId: String) {
        navigateToEventLanding(eventId)
    }

    override fun showTransactionDetails(eventId: String, marketplaceType: String) {
        navigateToTransaction(eventId, marketplaceType, navigation = TransactionNavigator.Navigation.DETAILS)
    }

    override fun showTransactionEdit(eventId: String, marketplaceType: String, offerGroupId: String) {
        navigateToTransaction(eventId, marketplaceType, offerGroupId, TransactionNavigator.Navigation.EDIT)
    }

    override fun showTransactionReviewFix(eventId: String, marketplaceType: String, offerGroupId: String) {
        navigateToTransaction(eventId, marketplaceType, offerGroupId, TransactionNavigator.Navigation.REVIEW_FIX_PAYMENT)
    }

    override fun showTransactionReviewClaim(eventId: String, marketplaceType: String, offerGroupId: String) {
        navigateToTransaction(eventId, marketplaceType, offerGroupId, TransactionNavigator.Navigation.REVIEW_CLAIM_TICKETS)
    }

    private fun navigateToAuthLogin(navigation: AuthNavigator.Navigation) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun navigateToAuthVerificationExpired(email: String, navigation: AuthNavigator.Navigation) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.EXTRA_EMAIL, email)
        intent.putExtra(AuthActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun navigateToAuthResetPassword(token: String, navigation: AuthNavigator.Navigation) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.EXTRA_TOKEN, token)
        intent.putExtra(AuthActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun navigateToFilter() {
        val intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSearchEvents(){
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent, createSearchTransitionAnimationOptions().toBundle())
    }

    private fun navigateToEventLanding(id: String) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra(EventActivity.ARG_EVENT_ID, id)
        startActivity(intent)
    }

    private fun navigateToPayments() {
        val intent = Intent(this, PaymentsActivity::class.java)
        intent.putExtra(PaymentsActivity.NAVIGATE_TO_PAYMENTS, PaymentsActivity.NAVIGATION.MANAGE)
        startActivity(intent)
    }

    private fun navigateToProfileCapture(unit: Unit) {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra(ProfileEditActivity.EXTRA_NAVIGATION, ProfileEditNavigator.Navigation.CAPTURE.name)
        startActivity(intent)
    }

    private fun navigateToProfileDetails(unit: Unit) {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra(ProfileEditActivity.EXTRA_NAVIGATION, ProfileEditNavigator.Navigation.DETAILS.name)
        startActivity(intent)
    }

    private fun navigateToProfileCaptureIntro(unit: Unit) {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra(ProfileEditActivity.EXTRA_NAVIGATION, ProfileEditNavigator.Navigation.INTRO.name)
        startActivity(intent)
    }

    private fun navigateToTransactionDetails(id: String, type: String) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_MARKETPLACE_TYPE, type)
        startActivity(intent)
    }

    private fun navigateToTransaction(id: String, type: String, offerGroupId: String? = null,
                                      navigation: TransactionNavigator.Navigation) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_MARKETPLACE_TYPE, type)
        intent.putExtra(TransactionActivity.EXTRA_OFFER_GROUP_ID, offerGroupId)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun navigateToTicketDetails(id: String, ticketId: String? = null) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_TICKET_ID, ticketId)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, TransactionNavigator.Navigation.TICKET_DETAILS.name)
        startActivity(intent)
    }

    private fun navigateToSettings(navigation: SettingsNavigator.Navigation) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun navigateToTicketAssignment(eventId: String, assignmentToken: String, email: String?) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, eventId)
        intent.putExtra(TransactionActivity.EXTRA_ASSIGNMENT_TOKEN, assignmentToken)
        intent.putExtra(TransactionActivity.EXTRA_EMAIL, email)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, TransactionNavigator.Navigation.ACCEPT_TOKEN.name)
        startActivity(intent/*, createLoopTransitionAnimationOptions().toBundle()*/)
    }

    private fun navigateToWebUrl(url: String?) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(ContextCompat.getColor(this, R.color.color_white))
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun createSearchTransitionAnimationOptions(): ActivityOptions {
        val searchBox = findViewById<LinearLayout>(R.id.search_box)
        val searchBoxIcon = findViewById<ImageView>(R.id.search_box_icon)
        val searchBoxText = findViewById<TextView>(R.id.search_box_text)

        return createActivityOptions(
            searchBox to  getString(R.string.transition_name_search_box),
            searchBoxIcon to getString(R.string.transition_name_search_box_icon),
            searchBoxText to getString(R.string.transition_name_search_box_text)
        )
    }

    companion object {
        private const val CONTAINER = R.id.container_main
    }
}
