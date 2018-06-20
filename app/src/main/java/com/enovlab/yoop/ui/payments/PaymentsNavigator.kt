package com.enovlab.yoop.ui.payments

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * @author vishaan
 */
class PaymentsNavigator @Inject constructor() : ViewModel() {
    internal val navigateToAddPayment = PublishSubject.create<Unit>()
    internal val navigateToEditPayment = PublishSubject.create<String>()
    internal val navigateBack = PublishSubject.create<Pair<Boolean, Long>>()
}