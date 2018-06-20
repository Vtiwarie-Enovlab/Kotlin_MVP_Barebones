package com.enovlab.yoop

import com.enovlab.yoop.inject.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import com.zopim.android.sdk.api.ZopimChat
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import zendesk.core.Zendesk

/**
 * Created by Max Toskhoparan on 2/14/2018.
 */
class YoopApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Zendesk.INSTANCE.init(this,
            BuildConfig.ZENDESK_BASE_URL, BuildConfig.ZENDESK_APP_ID, BuildConfig.ZENDESK_SDK_CLIENT_ID)

        ZopimChat.init(BuildConfig.ZENDESK_ACCOUNT_KEY)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}