package com.enovlab.yoop.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.settings.SettingsNavigator.Navigation.*
import com.enovlab.yoop.ui.settings.about.AboutFragment
import com.enovlab.yoop.ui.settings.notifications.NotificationsFragment
import com.enovlab.yoop.ui.settings.support.SupportFragment
import com.enovlab.yoop.utils.WeakHandler

class SettingsActivity : BaseActivity<SettingsNavigator>() {

    override val navigatorClass = SettingsNavigator::class.java

    private lateinit var weakHandler: WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        weakHandler = WeakHandler()

        onNewIntent(intent)
    }

    override fun setupNavigation(navigator: SettingsNavigator) {
        navigator.navigateBack.observeNavigation {
            weakHandler.postDelayed({ navigateBack(it.first) }, it.second)
        }
        navigator.navigateToNotifications.observeNavigation(::navigateToNotifications)
        navigator.navigateToSupport.observeNavigation(::navigateToSupport)
        navigator.navigateToAbout.observeNavigation(::navigateToAbout)
        navigator.restartApp.observeNavigation(::restartApp)
        navigator.navigateToWebUrl.observeNavigation(::navigateToWebUrl)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val navigation = intent.getStringExtra(EXTRA_NAVIGATION)?.let { valueOf(it) }
        when (navigation) {
            NOTIFICATIONS -> navigateToNotifications(Unit)
            SUPPORT -> navigateToSupport(Unit)
            ABOUT -> navigateToAbout(Unit)
            null -> navigateBack(true)
        }
    }

    private fun navigateToNotifications(unit: Unit) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, NotificationsFragment.newInstance())
            .commit()
    }

    private fun navigateToSupport(unit: Unit) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, SupportFragment.newInstance())
            .commit()
    }

    private fun navigateToAbout(unit: Unit) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, AboutFragment.newInstance())
            .commit()
    }

    private fun restartApp(unit: Unit) {
        val launchIntent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(launchIntent)
        finishAffinity()
    }

    private fun navigateToWebUrl(url: String?) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(ContextCompat.getColor(this, R.color.dark_grey))
            .setCloseButtonIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_32dp)!!.toBitmap())
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    companion object {
        const val EXTRA_NAVIGATION = "NAVIGATION"
        private val CONTAINER = R.id.container_settings
    }
}