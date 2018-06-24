package com.enovlab.yoop.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.*

class AppContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {

        fun wrap(context: Context, locale: Locale): ContextWrapper {
            Locale.setDefault(locale)

            val config = context.resources.configuration
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setSystemLocale(config, locale)
                else -> setSystemLocaleLegacy(config, locale)
            }

            return AppContextWrapper(context.createConfigurationContext(config))
        }

        @Suppress("DEPRECATION")
        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}