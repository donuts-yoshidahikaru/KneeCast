package com.tutorial.kneecast.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.tutorial.kneecast.BuildConfig
import com.tutorial.kneecast.data.model.GeocoderResponse

interface GeocoderApi {
    @GET("geocode/V1/geoCoder")
    suspend fun getCoordinates(
        @Query("appid") appId: String = BuildConfig.CLIENT_ID,
        @Query("query") address: String,
        @Query("output") output: String = "json"
    ): Response<GeocoderResponse>
}