package com.enovlab.yoop.data.manager

import android.content.Context
import android.preference.PreferenceManager
import com.enovlab.yoop.utils.ext.format
import com.enovlab.yoop.utils.ext.toLocale
import java.util.*
import javax.inject.Inject

class AppPreferencesImpl
@Inject constructor(context: Context) : AppPreferences {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var authToken: String?
        get() = preferences.getString(AUTH_TOKEN, null)
        set(value) {
            preferences.edit().putString(AUTH_TOKEN, value).apply()
        }

    override var firstLaunch: Boolean
        get() = preferences.getBoolean(FIRST_LAUNCH, true)
        set(value) {
            preferences.edit().putBoolean(FIRST_LAUNCH, value).apply()
        }

    override var locale: Locale
        get() = preferences.getString(LOCALE, DEFAULT_LOCALE).toLocale()
        set(value) {
            preferences.edit().putString(LOCALE, value.format()).apply()
        }

    override var resetEmail: String?
        get() = preferences.getString(RESET_EMAIL, null)
        set(value) {
            preferences.edit().putString(RESET_EMAIL, value).apply()
        }

    override var citiesLoadDate: Long
        get() = preferences.getLong(CITIES_LOAD_DATE, 0L)
        set(value) {
            preferences.edit().putLong(CITIES_LOAD_DATE, value).apply()
        }

    override var introSeen: Boolean
        get() = preferences.getBoolean(INTRO_SEEN, false)
        set(value) {
            preferences.edit().putBoolean(INTRO_SEEN, value).apply()
        }

    override var navigation: String?
        get() = preferences.getString(NAVIGATION, null)
        set(value) {
            preferences.edit().putString(NAVIGATION, value).apply()
        }

    override var signedUp: Boolean
        get() = preferences.getBoolean(SIGNED_UP, false)
        set(value) {
            preferences.edit().putBoolean(SIGNED_UP, value).apply()
        }

    override var assignmentDeepLink: String?
        get() = preferences.getString(ASSIGNMENT_DEEP_LINK, null)
        set(value) {
            preferences.edit().putString(ASSIGNMENT_DEEP_LINK, value).apply()
        }

    override fun clear() {
        preferences.edit().putString(AUTH_TOKEN, null).apply()
    }

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val FIRST_LAUNCH = "FIRST_LAUNCH"
        private const val LOCALE = "LOCALE"
        private const val RESET_EMAIL = "RESET_EMAIL"
        private const val CITIES_LOAD_DATE = "CITIES_LOAD_DATE"
        private const val INTRO_SEEN = "INTRO_SEEN"
        private const val DEFAULT_LOCALE = "en_us"
        private const val NAVIGATION = "NAVIGATION"
        private const val SIGNED_UP = "SIGNED_UP"
        private const val ASSIGNMENT_DEEP_LINK = "ASSIGNMENT_DEEP_LINK"
    }
}
