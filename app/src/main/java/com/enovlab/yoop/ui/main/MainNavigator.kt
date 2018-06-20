package com.enovlab.yoop.ui.main

import android.arch.lifecycle.ViewModel
import com.enovlab.yoop.ui.main.mytickets.MyTicketsNavigation
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/27/2018.
 */
class MainNavigator @Inject constructor() : ViewModel() {
    internal val navigateToDiscover = PublishSubject.create<Unit>()
    internal val navigateToMyTickets = PublishSubject.create<MyTicketsNavigation>()
    internal val navigateToProfile = PublishSubject.create<Unit>()
    internal val navigateToEventLanding = PublishSubject.create<String>()
    internal val navigateToFilter = PublishSubject.create<Unit>()
    internal val navigateToSearchEvents = PublishSubject.create<Unit>()
    internal val navigateToTransactionDetails = PublishSubject.create<Pair<String, String>>()
    internal val navigateToTransactionEdit = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToAuthLogin = PublishSubject.create<Unit>()
    internal val navigateToAuthSignup = PublishSubject.create<Unit>()
    internal val navigateToProfileDetails = PublishSubject.create<Unit>()
    internal val navigateToProfileCapture = PublishSubject.create<Unit>()
    internal val navigateToProfileCaptureIntro = PublishSubject.create<Unit>()
    internal val navigateToManagePayments = PublishSubject.create<Unit>()
    internal val navigateToNotifications = PublishSubject.create<Unit>()
    internal val navigateToSupport = PublishSubject.create<Unit>()
    internal val navigateToAbout = PublishSubject.create<Unit>()
    internal val navigateToTransactionFixPayment = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToTransactionClaimTickets = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToTicketDetails = PublishSubject.create<Pair<String, String>>()
    internal val navigateToWebUrl = PublishSubject.create<String>()
    internal val navigateThroughDeepLink = PublishSubject.create<String>()

    enum class Navigation {
        DISCOVER, MY_TICKETS, PROFILE
    }
}