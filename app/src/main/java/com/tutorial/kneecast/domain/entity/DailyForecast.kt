package com.tutorial.kneecast.domain.entity

data class DailyForecast(
    val date: String, // Consider using a more specific Date/Time type later if needed
    val iconId: Int,
    val minTemp: Double,
    val maxTemp: Double
    // Add any other relevant fields like weather description, humidity, etc.
    // For now, keeping it aligned with the previous DailySubWeather and DailyData
)
