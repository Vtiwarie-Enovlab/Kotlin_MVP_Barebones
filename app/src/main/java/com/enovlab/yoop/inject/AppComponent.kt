package com.enovlab.yoop.inject

import com.enovlab.yoop.YoopApp
import com.enovlab.yoop.fcm.YoopFirebaseInstanceIdBuilder
import com.enovlab.yoop.inject.api.GoogleServiceModule
import com.enovlab.yoop.inject.api.PaysafeServiceModule
import com.enovlab.yoop.inject.api.YoopServiceModule
import com.enovlab.yoop.inject.viewmodel.ViewModelBuilder
import com.enovlab.yoop.ui.auth.AuthBuilder
import com.enovlab.yoop.ui.event.EventBuilder
import com.enovlab.yoop.ui.transaction.TransactionBuilder
import com.enovlab.yoop.ui.filter.FilterBuilder
import com.enovlab.yoop.ui.main.MainBuilder
import com.enovlab.yoop.ui.payments.PaymentsBuilder
import com.enovlab.yoop.ui.profile.ProfileEditBuilder
import com.enovlab.yoop.ui.search.SearchBuilder
import com.enovlab.yoop.ui.settings.SettingsBuilder
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Singleton
@Component(modules = arrayOf(
    AndroidSupportInjectionModule::class,
    AppModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    YoopServiceModule::class,
    PaysafeServiceModule::class,
    GoogleServiceModule::class,
    ViewModelBuilder::class,
    MainBuilder::class,
    YoopFirebaseInstanceIdBuilder::class,
    AuthBuilder::class,
    EventBuilder::class,
    AuthBuilder::class,
    FilterBuilder::class,
    SearchBuilder::class,
    TransactionBuilder::class,
    PaymentsBuilder::class,
    ProfileEditBuilder::class,
    SettingsBuilder::class,
    BleModule::class
))
interface AppComponent : AndroidInjector<YoopApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<YoopApp>()
}
