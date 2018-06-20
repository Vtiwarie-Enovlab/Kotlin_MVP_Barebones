package com.enovlab.yoop.ui.profile

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ProfileEditNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Pair<Boolean, Long>>()
    internal val navigateToCapture = PublishSubject.create<Unit>()
    internal val navigateToIntro = PublishSubject.create<Unit>()

    enum class Navigation {
        DETAILS, CAPTURE, INTRO
    }
}