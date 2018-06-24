package com.enovlab.yoop.utils.ext

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresPermission
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import timber.log.Timber
import java.util.*

fun <T : Context> T.displaySize(): Pair<Int, Int> {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x to size.y
}

fun <T : Context> T.dpToPixels(dp: Float): Float {
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun <T : Context> T.pixelsToDp(pixels: Float): Float {
    val metrics = resources.displayMetrics
    return pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun <T : Context> T.requiresLocationPermission(): Boolean {
    return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
}

fun <T : Context> T.requiresCalendarPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
}

fun <T : Context> T.requiresCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
}

fun <T : Context> T.requiresContactsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
}

fun <T : Context> T.navigationBarHeight(): Int {
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0 && !hasMenuKey) resources.getDimensionPixelSize(resourceId) else 0
}

fun Date.isLessThan24HoursLeft(): Boolean {
    val date24 = Calendar.getInstance()
    date24.add(Calendar.HOUR_OF_DAY, 24)
    return before(date24.time)
}

fun Date.isLessThan48HoursLeft(): Boolean {
    val date48 = Calendar.getInstance()
    date48.add(Calendar.HOUR_OF_DAY, 48)
    return before(date48.time)
}

fun ContentResolver.query(@RequiresPermission.Read uri: Uri,
                          projection: Array<String>? = null,
                          block: (Cursor) -> Unit) {
    query(uri, projection, null, null, null).use {
        block(it)
    }
}

fun <T : Activity> T.createActivityOptions(vararg pairs: Pair<View, String>): ActivityOptions {
    val array = pairs.map { android.util.Pair.create(it.first, it.second) }.toTypedArray()
    return ActivityOptions.makeSceneTransitionAnimation(this, *array)
}


fun Context.vectorToBitmap(@DrawableRes resId: Int): Bitmap {
    val vectorDrawable = ContextCompat.getDrawable(this, resId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return bitmap
}

fun <T : Fragment> T.startPostponedDelayedEnterTransition(delay: Long = 200L) {
    Handler().postDelayed({ startPostponedEnterTransition() }, delay)
}

fun <T : Activity> T.keyboardListener(listener: (Boolean) -> Unit): Unregistrar {
    return KeyboardVisibilityEvent.registerEventListener(this) { listener(it) }
}

fun <T : Activity> T.keyboardAutoListener(listener: (Boolean) -> Unit) {
    KeyboardVisibilityEvent.setEventListener(this) { listener(it) }
}

fun <T : Activity> T.registerLifecycleCallbacks(
    onActivityPaused: ((activity: Activity) -> Unit)? = null,
    onActivityResumed: ((activity: Activity) -> Unit)? = null,
    onActivityStarted: ((activity: Activity) -> Unit)? = null,
    onActivityDestroyed: ((activity: Activity) -> Unit)? = null,
    onActivitySaveInstanceState: ((activity: Activity, outState: Bundle?) -> Unit)? = null,
    onActivityStopped: ((activity: Activity) -> Unit)? = null,
    onActivityCreated: ((activity: Activity, savedInstanceState: Bundle?) -> Unit)? = null
): Application.ActivityLifecycleCallbacks {
    val callback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
            onActivityPaused?.invoke(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            onActivityResumed?.invoke(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            onActivityStarted?.invoke(activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            onActivityDestroyed?.invoke(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            onActivitySaveInstanceState?.invoke(activity, outState)
        }

        override fun onActivityStopped(activity: Activity) {
            onActivityStopped?.invoke(activity)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            onActivityCreated?.invoke(activity, savedInstanceState)
        }
    }
    application.registerActivityLifecycleCallbacks(callback)
    return callback
}

fun String.toLocale(): Locale {
    try {
        val parts = split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return Locale(parts[0], parts[1])
    } catch (e: Exception) {
        Timber.e(e)
        return Locale.getDefault()
    }
}

fun Locale.format(): String {
    return "${language}_$country"
}

fun ByteArray.hexString(): String {
    val hex = StringBuilder(2 * this.size)
    for (b: Byte in this) {
        hex.append(HEXES[b.toInt() and 0xF0 shr 4]).append(HEXES[b.toInt() and 0x0F])
    }
    return hex.toString()
}

fun ByteArray.hex(): Long {
    return hexString().toLong(16)
}

internal const val HEXES: String = "0123456789ABCDEF"