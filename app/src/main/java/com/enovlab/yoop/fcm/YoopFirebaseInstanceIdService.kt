package com.enovlab.yoop.fcm

import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.utils.RxSchedulers
import com.enovlab.yoop.utils.ext.plusAssign
import com.google.firebase.iid.FirebaseInstanceIdService
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/25/2018.
 */
class YoopFirebaseInstanceIdService : FirebaseInstanceIdService() {

    @Inject lateinit var yoopService: YoopService
    @Inject lateinit var schedulers: RxSchedulers
    @Inject lateinit var preferences: AppPreferences

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onTokenRefresh() {
        if (preferences.authToken != null) {
            disposables += yoopService.sendPushNotificationsToken()
                .subscribeOn(schedulers.network)
                .subscribe({ Timber.i("FCM token sent successfully.") }, { Timber.e(it) })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}