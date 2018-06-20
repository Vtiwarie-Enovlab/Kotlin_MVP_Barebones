package com.enovlab.yoop.inject.api

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.api.PaysafeService
import com.enovlab.yoop.api.interceptor.PaysafeAuthInterceptor
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
class PaysafeServiceModule {

    @Singleton
    @Provides
    @Named("paysafe_auth")
    fun provideAuthInterceptor(interceptor: PaysafeAuthInterceptor): Interceptor {
        return interceptor
    }

    @Singleton
    @Provides
    @Named("paysafe_client")
    fun provideOkHttpClient(@Named("paysafe_auth") authInterceptor: Interceptor,
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
    fun providePaysafeService(@Named("paysafe_client") okHttpClient: OkHttpClient,
                              gson: Gson): PaysafeService {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.PAYSAFE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PaysafeService::class.java)
    }
}