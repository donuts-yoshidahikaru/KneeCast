package com.tutorial.kneecast.data.model

data class WeatherResponse(
    val lat: String,
    val lon: String,
    val elevation: Int,
    val timezone: String,
    val units: String,
    val current: CurrentWeather,
    val hourly: HourlyWeather?,
    val daily: DailyWeather?
)

data class CurrentWeather(
    val icon: String,
    val icon_num: Int,
    val summary: String,
    val temperature: Double,
    val wind: Wind,
    val precipitation: Precipitation,
    val cloud_cover: Int
)

data class HourlyWeather(
    val data: List<HourlyData>
)

data class HourlyData(
    val date: String,
    val weather: String,
    val icon: Int,
    val summary: String,
    val temperature: Double,
    val wind: Wind,
    val cloud_cover: CloudCover,
    val precipitation: Precipitation
)

data class Wind(
    val speed: Double,
    val angle: Int,
    val dir: String
)

data class Precipitation(
    val total: Double,
    val type: String
)

data class CloudCover(
    val total: Int
)

data class DailyWeather(
    val data: List<DailyData>
)

data class DailyData(
    val day: String,
    val weather: String,
    val icon: Int,
    val summary: String,
    val all_day: DailySubWeather,
    val morning: DailySubWeather?,
    val afternoon: DailySubWeather?,
    val evening: DailySubWeather?
)

data class DailySubWeather(
    val weather: String,
    val icon: Int,
    val temperature: Double,
    val temperature_min: Double,
    val temperature_max: Double,
    val wind: Wind,
    val cloud_cover: CloudCover,
    val precipitation: Precipitation
)