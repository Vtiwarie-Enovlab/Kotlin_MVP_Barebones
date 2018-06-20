package com.enovlab.yoop.api.interceptor

import com.enovlab.yoop.api.GoogleApiService
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
class GoogleApiUrlInterceptor
@Inject constructor(@Named("google_key") private val apiKey: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val host = GoogleApiService.BASE_URL
            .replace("$SCHEME://", "")
            .replace("/", "")
        val url = chain.request().url()
            .newBuilder()
            .scheme(SCHEME).host(host)
            .addQueryParameter(QUERY_KEY, apiKey)
            .build()
        val request = chain.request()
            .newBuilder()
            .url(url).build()
        return chain.proceed(request)
    }

    companion object {
        private const val SCHEME = "https"
        private const val QUERY_KEY = "key"
    }
}