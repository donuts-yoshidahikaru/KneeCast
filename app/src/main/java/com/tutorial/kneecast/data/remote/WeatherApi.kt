package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.tutorial.kneecast.BuildConfig

interface WeatherApi {
    @GET("weather/V1/place")
    suspend fun getWeather(
        @Query("appid") appId: String = BuildConfig.CLIENT_ID,
        @Query("coordinates") coordinates: String,
        @Query("output") output: String = "json"
    ): Response<WeatherResponse>
}