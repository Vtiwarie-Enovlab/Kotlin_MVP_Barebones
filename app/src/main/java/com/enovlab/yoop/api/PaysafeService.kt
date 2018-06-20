package com.enovlab.yoop.api

import com.enovlab.yoop.api.request.SingleUseTokenRequest
import com.enovlab.yoop.api.response.payment.SingleUseToken
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Max Toskhoparan on 1/12/2018.
 */
interface PaysafeService {

    @POST("/customervault/v1/singleusetokens")
    fun getToken(@Body request: SingleUseTokenRequest): Single<SingleUseToken>
}