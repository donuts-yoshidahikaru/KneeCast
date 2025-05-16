package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.tutorial.kneecast.BuildConfig

interface WeatherApi {
    @GET("api/v1/free/point")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("sections") sections: String = "all",
        @Query("language") language: String = "en",
        @Query("units") units: String = "metric",
        @Query("key") key: String = BuildConfig.METEOSOURCE_API_KEY
    ): Response<WeatherResponse>
}