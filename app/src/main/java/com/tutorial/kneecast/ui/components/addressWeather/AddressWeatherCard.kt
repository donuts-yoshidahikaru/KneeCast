package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.runtime.Composable
import com.tutorial.kneecast.data.model.Feature

@Composable
fun AddressWeatherCard(address: Feature) {
    val (lngStr, latStr) = address.Geometry.Coordinates.split(",").map { it.trim() } + listOf("", "")
    val longitude = lngStr.toDoubleOrNull()
    val latitude = latStr.toDoubleOrNull()

    if (longitude != null && latitude != null) {
        WeatherDisplay(address.Name, longitude, latitude)
    }
}