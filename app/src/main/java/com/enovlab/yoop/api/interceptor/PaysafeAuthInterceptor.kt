package com.enovlab.yoop.api.interceptor

import android.util.Base64
import com.enovlab.yoop.BuildConfig
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/16/2018.
 */
class PaysafeAuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val username = BuildConfig.PAYSAFE_USERNAME
        val password = BuildConfig.PAYSAFE_PASSWORD

        val authToken: String = Base64.encodeToString(("$username:$password").toByteArray(charset(CHARSET)), Base64.NO_WRAP)

        val builder = Headers.Builder()
        builder.add(HEADER_AUTHORIZATION, "$HEADER_BASIC $authToken")

        val original = chain.request()
        val request = original.newBuilder()
            .headers(builder.build())
            .method(original.method(), original.body())
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val CHARSET = "UTF-8"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_BASIC = "Basic"
    }
}