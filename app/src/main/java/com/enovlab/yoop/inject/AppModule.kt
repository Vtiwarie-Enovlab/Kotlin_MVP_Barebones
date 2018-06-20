package com.enovlab.yoop.inject

import android.content.Context
import com.enovlab.yoop.YoopApp
import com.enovlab.yoop.data.manager.*
import com.enovlab.yoop.utils.RxSchedulers
import com.google.firebase.iid.FirebaseInstanceId
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

@Module
class AppModule {

    @Provides
    fun provideContext(app: YoopApp): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRxSchedulers() = RxSchedulers()

    @Provides
    @Singleton
    fun provideAppPreferences(appPreferences: AppPreferencesImpl): AppPreferences = appPreferences

    @Provides
    @Singleton
    @Named("device_id")
    fun provideDeviceId(): String? = FirebaseInstanceId.getInstance().id

    @Provides
    @Singleton
    fun provideDataManager(manager: DataMangerImpl): DataManager = manager

    @Provides
    @Singleton
    fun provideFileManager(manager: FileManagerImpl): FileManager = manager

    @Provides
    @Singleton
    fun provideKeyStoreManager(manager: KeyStoreManagerImpl): KeyStoreManager = manager
}
