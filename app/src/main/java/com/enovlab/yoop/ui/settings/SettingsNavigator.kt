package com.enovlab.yoop.ui.settings

import android.arch.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class SettingsNavigator @Inject constructor() : ViewModel() {
    internal val navigateBack = PublishSubject.create<Pair<Boolean, Long>>()
    internal val navigateToNotifications = PublishSubject.create<Unit>()
    internal val navigateToSupport = PublishSubject.create<Unit>()
    internal val navigateToAbout = PublishSubject.create<Unit>()
    internal val restartApp = PublishSubject.create<Unit>()
    internal val navigateToWebUrl = PublishSubject.create<String>()

    enum class Navigation {
        NOTIFICATIONS, SUPPORT, ABOUT
    }
}