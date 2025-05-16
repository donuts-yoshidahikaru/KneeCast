package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.ui.components.WeatherScreen
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModelFactory

@Composable
fun WeatherDisplay(addressName: String, longitude: Double, latitude: Double) {

    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val factory = remember { WeatherViewModelFactory() }

    val weatherViewModel: WeatherViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = factory,
        key = "address_weather_${addressName}_${longitude}_${latitude}"
    )

    val coordinates = remember(longitude, latitude) {
        Coordinates(latitude, longitude)
    }

    LaunchedEffect(coordinates) {
        weatherViewModel.fetchWeatherInfo(addressName, coordinates)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        WeatherScreen(weatherViewModel = weatherViewModel)
    }
}