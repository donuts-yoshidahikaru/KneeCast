package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GoWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Goバックエンドサーバーの/weatherエンドポイントにアクセスするためのAPI定義
 */
interface WeatherGoApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<GoWeatherResponse>
} 