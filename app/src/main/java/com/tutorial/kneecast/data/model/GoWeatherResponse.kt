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
    @SerializedName("temperature_min") val temperatureMin: Double?,
    @SerializedName("daily") val daily: GoDailyWeather?
)

/**
 * 日毎の天気情報（7日間データ）
 */
data class GoDailyWeather(
    @SerializedName("data") val data: List<GoDailyData>
)

/**
 * 1日分の天気情報
 */
data class GoDailyData(
    @SerializedName("day") val day: String,
    @SerializedName("all_day") val allDay: GoDailySubWeather
)

/**
 * 1日の詳細な天気情報
 */
data class GoDailySubWeather(
    @SerializedName("icon") val icon: Int,
    @SerializedName("temperature_min") val temperatureMin: Double,
    @SerializedName("temperature_max") val temperatureMax: Double
) 