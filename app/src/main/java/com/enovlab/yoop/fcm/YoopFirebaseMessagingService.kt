package com.enovlab.yoop.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.text.Html
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Max Toskhoparan on 1/25/2018.
 */
class YoopFirebaseMessagingService : FirebaseMessagingService() {

    private val id = AtomicInteger(0)

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        if (data.isNotEmpty()) {
            val title = data[KEY_TITLE]
            val messageBody = data[KEY_MESSAGE]

            var deepLink: String? = null
            try {
                deepLink = JSONObject(data[KEY_NAME]).getString(KEY_DEEP_LINK)
            } catch (e: Exception) {
                Timber.e(e)
            }

            showNotification(title, messageBody, deepLink)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun showNotification(title: String?, message: String?, deepLink: String?) {
        val intent = Intent(this, MainActivity::class.java)
        if (deepLink != null && deepLink.isNotEmpty()) {
            intent.data = Uri.parse(Uri.decode(Html.escapeHtml(deepLink)))
        }

        val pendingIntent = PendingIntent.getActivity(this,
            0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val channel = NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT).apply {
//                    description = getString(R.string.first_launch_title)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
                NotificationCompat.Builder(this, CHANNEL_ID)
            }
            else -> NotificationCompat.Builder(this)
        }
        val notification = builder
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.getAndIncrement(), notification)
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_DEEP_LINK = "deepLink"
        private const val KEY_NAME = "enovlab"
        private const val CHANNEL_ID = "9379992"
        private const val TOPIC = "global"
    }
}