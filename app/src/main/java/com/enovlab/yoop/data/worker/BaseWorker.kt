package com.enovlab.yoop.data.worker

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import androidx.work.Worker
import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.interceptor.BaseAuthInterceptor
import com.enovlab.yoop.data.YoopDatabase
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.data.manager.AppPreferencesImpl
import com.enovlab.yoop.inject.NetworkModule
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Since Dagger is not supported yet, we'll use a manual initialization
abstract class BaseWorker : Worker() {

    protected lateinit var database: YoopDatabase
    protected lateinit var service: YoopService

    abstract fun continueWork(): WorkerResult

    override fun doWork(): WorkerResult {
        database = provideDatabase(applicationContext)
        service = provideYoopService(applicationContext)
        return continueWork()
    }

    private fun provideDatabase(context: Context): YoopDatabase {
        val builder = Room.databaseBuilder(context, YoopDatabase::class.java, "yoop.db")
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    private fun provideYoopService(context: Context): YoopService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(YoopService::class.java)
    }

    private fun provideDeviceId(): String? = FirebaseInstanceId.getInstance().id

    private fun provideAppPreferences(context: Context): AppPreferences {
        return AppPreferencesImpl(context)
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat(NetworkModule.DATE_FORMAT)
            .create()
    }

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    private fun provideAuthInterceptor(context: Context): Interceptor {
        return BaseAuthInterceptor(provideAppPreferences(context), provideDeviceId())
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(provideAuthInterceptor(context))
            .addInterceptor(provideHttpLoggingInterceptor())
            .build()
    }
}