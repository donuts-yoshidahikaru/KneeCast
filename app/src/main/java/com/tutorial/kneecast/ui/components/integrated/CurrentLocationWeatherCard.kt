package com.tutorial.kneecast.ui.components.integrated

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
import timber.log.Timber
import android.location.Location

/**
 * 現在地の天気情報を表示するカードコンポーネント
 */
@Composable
fun CurrentLocationWeatherCard(coordinates: Coordinates) {
    // 固有のキーでWeatherViewModelを取得
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val factory = remember { WeatherViewModelFactory() }
    
    val weatherViewModel: WeatherViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = factory,
        key = "current_location_weather_${coordinates.latitude}_${coordinates.longitude}"
    )
    
    LaunchedEffect(coordinates) {
        Timber.d("現在地の天気情報を取得: ${coordinates.latitude}, ${coordinates.longitude}")
        weatherViewModel.fetchWeatherInfo("現在地", coordinates)
    }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        WeatherScreen(weatherViewModel = weatherViewModel)
    }
}

/**
 * Locationオブジェクトから現在地の天気情報を表示するカードコンポーネント
 */
@Composable
fun CurrentLocationWeatherCard(location: Location?) {
    if (location == null) {
        return
    }
    
    val coordinates = remember(location) {
        Coordinates(location.latitude, location.longitude)
    }
    
    CurrentLocationWeatherCard(coordinates)
} 