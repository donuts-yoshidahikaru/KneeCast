package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tutorial.kneecast.data.mapper.toCoordinates
import com.tutorial.kneecast.data.model.Feature

@Composable
fun AddressWeatherCard(address: Feature) {
    val coordinates = remember(address) { address.toCoordinates() } ?: return
    WeatherDisplay(address.Name, coordinates.longitude, coordinates.latitude)
}