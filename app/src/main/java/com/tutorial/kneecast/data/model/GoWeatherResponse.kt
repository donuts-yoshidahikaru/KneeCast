package com.tutorial.kneecast.data.model

import com.google.gson.annotations.SerializedName

/**
 * Goバックエンドサーバーから返される天気情報のレスポンスモデル
 */
data class GoWeatherResponse(
    @SerializedName("location_name") val locationName: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("target_date") val targetDate: String,
    @SerializedName("weather_summary") val weatherSummary: String?,
    @SerializedName("temperature_max") val temperatureMax: Double?,
    @SerializedName("temperature_min") val temperatureMin: Double?
) 