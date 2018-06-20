package com.enovlab.yoop.ui.event

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.enovlab.yoop.data.entity.CalenderInfo
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * @author vishaan
 */
class EventNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Boolean>()
    internal val navigateToDiscover = PublishSubject.create<Unit>()
    internal val navigateToEventLanding = PublishSubject.create<String>()
    internal val navigateToCalendar = PublishSubject.create<CalenderInfo>()
    internal val navigateToShare = PublishSubject.create<Pair<Uri, String>>()
    internal val navigateToTransactionDetails = PublishSubject.create<Pair<String, String>>()
    internal val navigateToTransactionEdit = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToGoogleMapsApp = PublishSubject.create<String>()
    internal val navigateToTicketDetails = PublishSubject.create<Pair<String, String>>()
    internal val navigateToTransactionFixPayment = PublishSubject.create<Triple<String, String, String>>()
    internal val navigateToTransactionClaimTickets = PublishSubject.create<Triple<String, String, String>>()
}