package com.enovlab.yoop.inject.api

import android.content.Context
import com.enovlab.yoop.R
import com.enovlab.yoop.api.GoogleApiService
import com.enovlab.yoop.api.interceptor.GoogleApiUrlInterceptor
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

@Module
class GoogleServiceModule {

    @Singleton
    @Provides
    @Named("google_key")
    fun provideGoogleApiKey(context: Context): String {
        return context.getString(R.string.google_api_key)
    }

    @Singleton
    @Provides
    @Named("google_url")
    fun provideGoogleMapsUrlInterceptor(interceptor: GoogleApiUrlInterceptor): Interceptor {
        return interceptor
    }

    @Singleton
    @Provides
    @Named("google_api")
    fun provideGoogleMapsOkHttpClient(@Named("google_url") urlInterceptor: Interceptor,
                                      loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(urlInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleMapsService(@Named("google_api") okHttpClient: OkHttpClient): GoogleApiService {
        return Retrofit.Builder()
            .baseUrl(GoogleApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GoogleApiService::class.java)
    }

    companion object {
        private val CONNECTION_TIMEOUT = 30L
    }
}