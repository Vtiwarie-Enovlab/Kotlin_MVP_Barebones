package com.enovlab.yoop.api

import com.enovlab.yoop.api.response.maps.MapResults
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Max Toskhoparan on 1/18/2018.
 */
interface GoogleApiService {

    @GET("/maps/api/geocode/json")
    fun geocodeAddress(@Query("latlng") latlng: String): Flowable<MapResults>

    companion object {
        const val BASE_URL = "https://maps.googleapis.com"
    }
}