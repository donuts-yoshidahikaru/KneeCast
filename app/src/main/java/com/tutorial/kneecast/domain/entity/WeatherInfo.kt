package com.tutorial.kneecast.domain.entity

data class WeatherInfo(
    val locationName: String?, // Optional: if we want to store the location name with weather
    val dailyForecasts: List<DailyForecast>
)
