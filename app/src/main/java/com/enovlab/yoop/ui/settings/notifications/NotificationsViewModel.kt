package com.enovlab.yoop.ui.settings.notifications

import com.enovlab.yoop.api.response.settings.NotificationSettings
import com.enovlab.yoop.data.repository.UserRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem.HeaderNotificationItem
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem.SettingsNotificationItem
import com.enovlab.yoop.ui.settings.notifications.language.LanguageItem
import com.enovlab.yoop.utils.ext.toCompletable
import com.enovlab.yoop.utils.ext.toLocale
import java.util.*
import javax.inject.Inject

class NotificationsViewModel
@Inject constructor(private val repository: UserRepository) : StateViewModel<NotificationsView>() {

    private var selectedLocale: Locale? = null

    override fun start() {
        load {
            repository.notificationSettings()
                .observeOn(schedulers.disk)
                .map(::mapToAdapterItems)
                .observeOn(schedulers.main)
                .doOnNext {
                    view?.showNotificationSettings(it)
                }
                .toCompletable()
        }

        view?.showCurrentLanguage(preferences.locale.displayLanguage)
        view?.showSupportedLanguages(supportedLanguages())
    }

    internal fun notificationSettingsChanged(item: SettingsNotificationItem) {
        action { repository.updateNotificationSettings(item.type, item.group, item.enabled) }
    }

    internal fun languageSelected(item: LanguageItem) {
        view?.showLanguagesDialog(false)
        if (!item.selected) {
            selectedLocale = item.locale
            view?.showRestartRequired()
        }
    }

    internal fun restartClicked() {
        if (selectedLocale != null) {
            preferences.locale = selectedLocale!!
            view?.showRestartApp()
        }
    }

    private fun supportedLanguages(): List<LanguageItem> {
        val currentLocale = preferences.locale
        return SUPPORTED_LOCALES.map {
            val locale = it.toLocale()
            LanguageItem(locale, locale == currentLocale)
        }
    }

    private fun mapToAdapterItems(list: List<NotificationSettings>): List<NotificationItem> {
        val items = mutableListOf<NotificationItem>()

        val marketplace = list.filter { it.group == NotificationSettings.Group.MARKETPLACE }.sortedBy { it.type }
        val payments = list.filter { it.group == NotificationSettings.Group.PAYMENT }.sortedBy { it.type }

        if (marketplace.isNotEmpty()) {
            items.add(HeaderNotificationItem(marketplace.first().title))
            marketplace.forEach {
                items.add(SettingsNotificationItem(it.type, it.shouldSend, it.group, it.description))
            }
        }

        if (payments.isNotEmpty()) {
            items.add(HeaderNotificationItem(payments.first().title))
            payments.forEach {
                items.add(SettingsNotificationItem(it.type, it.shouldSend, it.group, it.description))
            }
        }

        return items
    }

    companion object {
        private val SUPPORTED_LOCALES = arrayOf("en_us", "fr_ca")
    }
}