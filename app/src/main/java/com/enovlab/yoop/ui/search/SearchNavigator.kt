package com.enovlab.yoop.ui.search

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Boolean>()
    internal val navigateToEventLanding = PublishSubject.create<String>()
}