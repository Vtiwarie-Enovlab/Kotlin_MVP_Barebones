package com.enovlab.yoop.data.manager

import java.util.*

interface AppPreferences {
    var authToken: String?
    var firstLaunch: Boolean
    var locale: Locale
    var resetEmail: String?
    var citiesLoadDate: Long
    var introSeen: Boolean
    var navigation: String?
    var signedUp: Boolean
    var assignmentDeepLink: String?
    fun clear()
}
