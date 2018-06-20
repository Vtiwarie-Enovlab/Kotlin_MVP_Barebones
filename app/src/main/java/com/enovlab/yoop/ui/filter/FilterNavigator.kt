package com.enovlab.yoop.ui.filter

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by mtosk on 3/14/2018.
 */
class FilterNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Boolean>()
    internal val navigateToFilterDetails = PublishSubject.create<Unit>()
    internal val navigateToSearch = PublishSubject.create<Unit>()
}