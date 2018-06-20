package com.enovlab.yoop.fcm

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Max Toskhoparan on 1/25/2018.
 */

@Module
abstract class YoopFirebaseInstanceIdBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeFirebaseInstanceIdService(): YoopFirebaseInstanceIdService
}