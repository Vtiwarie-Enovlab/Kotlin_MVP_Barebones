package com.enovlab.yoop.ui.settings.notifications

import com.enovlab.yoop.ui.base.state.StateView
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem
import com.enovlab.yoop.ui.settings.notifications.language.LanguageItem

interface NotificationsView : StateView {
    fun showNotificationSettings(items: List<NotificationItem>)
    fun showCurrentLanguage(language: String)
    fun showSupportedLanguages(languages: List<LanguageItem>)
    fun showLanguagesDialog(active: Boolean)
    fun showRestartRequired()
    fun showRestartApp()
}