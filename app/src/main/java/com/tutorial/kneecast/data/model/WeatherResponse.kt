package com.tutorial.kneecast.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("daily") val daily: DailyWeather?
)

data class DailyWeather(
    @SerializedName("data") val data: List<DailyData>
)

data class DailyData(
    @SerializedName("day") val day: String,
    @SerializedName("all_day") val allDay: DailySubWeather
)

data class DailySubWeather(
    @SerializedName("icon") val icon: Int,
    @SerializedName("temperature_min") val temperatureMin: Double,
    @SerializedName("temperature_max") val temperatureMax: Double,
)