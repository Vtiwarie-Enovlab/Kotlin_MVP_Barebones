package com.enovlab.yoop.api.interceptor

import com.enovlab.yoop.BuildConfig
import com.enovlab.yoop.data.manager.AppPreferences
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
class BaseAuthInterceptor
@Inject constructor(private val preferences: AppPreferences,
                    @Named("device_id") private val deviceId: String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = preferences.authToken
        val pushToken = FirebaseInstanceId.getInstance().token

        val builder = Headers.Builder()
        builder.apply {
            add(HEADER_LOCALE, preferences.locale.toString())
            add(HEADER_VERSION, BuildConfig.VERSION_NAME)
            if (authToken != null) add(HEADER_AUTHORIZATION, "$HEADER_BEARER $authToken")
            if (pushToken != null) add(HEADER_DEVICE_TOKEN, pushToken)
            if (deviceId != null) add(HEADER_DEVICE_ID, deviceId)
            add(HEADER_DEVICE_TYPE, HEADER_DEVICE_TYPE_ANDROID)
        }
        val original = chain.request()
        val request = original.newBuilder()
            .headers(builder.build())
            .method(original.method(), original.body())
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_LOCALE = "Accept-Language"
        const val HEADER_VERSION = "Android-App-Version"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_BEARER = "Bearer"
        const val HEADER_DEVICE_TOKEN = "device-token"
        const val HEADER_DEVICE_TYPE = "device-type"
        const val HEADER_DEVICE_ID = "device-id"
        const val HEADER_DEVICE_TYPE_ANDROID = "ANDROID"
    }
}