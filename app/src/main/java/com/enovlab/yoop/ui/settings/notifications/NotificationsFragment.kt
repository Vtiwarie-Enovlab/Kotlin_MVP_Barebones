package com.enovlab.yoop.ui.settings.notifications

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.settings.SettingsFragment
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationAdapter
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItem
import com.enovlab.yoop.ui.settings.notifications.adapter.NotificationItemDecoration
import com.enovlab.yoop.ui.settings.notifications.language.LanguageAdapter
import com.enovlab.yoop.ui.settings.notifications.language.LanguageItem
import com.enovlab.yoop.ui.settings.notifications.language.LanguageItemDecoration
import kotlinx.android.synthetic.main.fragment_settings_notifications.*
import kotlinx.android.synthetic.main.layout_settings_app_bar.*
import kotlinx.android.synthetic.main.layout_settings_language_confirm.*
import kotlinx.android.synthetic.main.layout_settings_languages.*

class NotificationsFragment : SettingsFragment<NotificationsView, NotificationsViewModel>(), NotificationsView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = NotificationsViewModel::class.java

    private val notificationAdapter by lazy { NotificationAdapter() }
    private val languageAdapter by lazy { LanguageAdapter() }
    private val languageDialog by lazy { BottomSheetDialog(context!!) }
    private val confirmDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_back.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        settings_title.setText(R.string.settings_title_preferences)

        notificationAdapter.listener = viewModel::notificationSettingsChanged
        notification_list.adapter = notificationAdapter
        notification_list.addItemDecoration(NotificationItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))

        languageDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_settings_languages, null))
        languageAdapter.listener = viewModel::languageSelected
        languageDialog.language_list.adapter = languageAdapter
        languageDialog.language_list.addItemDecoration(
            LanguageItemDecoration(ContextCompat.getDrawable(context!!, R.drawable.divider_languages)!!,
                resources.getDimensionPixelSize(R.dimen.padding_large)))

        confirmDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_settings_language_confirm, null))
        confirmDialog.confirm_cancel.setOnClickListener { confirmDialog.hide() }
        confirmDialog.confirm_confirm.setOnClickListener { viewModel.restartClicked() }

        input_language.setOnClickListener {
            languageDialog.show()
        }
    }

    override fun showNotificationSettings(items: List<NotificationItem>) {
        notificationAdapter.submitList(items)
    }

    override fun showCurrentLanguage(language: String) {
        input_language.setText(language)
    }

    override fun showSupportedLanguages(languages: List<LanguageItem>) {
        languageAdapter.submitList(languages)
    }

    override fun showLanguagesDialog(active: Boolean) {
        languageDialog.hide()
    }

    override fun showRestartRequired() {
        confirmDialog.show()
    }

    override fun showRestartApp() {
        navigator.restartApp.go()
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }
}