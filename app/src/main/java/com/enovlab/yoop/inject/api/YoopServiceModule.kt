package com.enovlab.yoop.inject.api

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.interceptor.BaseAuthInterceptor
import com.enovlab.yoop.inject.NetworkModule
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Max Toskhoparan on 2/15/2018.
 */

@Module
class YoopServiceModule {

    @Singleton
    @Provides
    @Named("base_auth")
    fun provideAuthInterceptor(interceptor: BaseAuthInterceptor): Interceptor {
        return interceptor
    }

    @Singleton
    @Provides
    @Named("base_client")
    fun provideOkHttpClient(@Named("base_auth") authInterceptor: Interceptor,
                            loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
            .connectTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkModule.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideYoopService(@Named("base_client") okHttpClient: OkHttpClient,
                           gson: Gson): YoopService {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(YoopService::class.java)
    }
}