package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    val weatherResponse by weatherViewModel.weatherResponse.observeAsState()
    val loading by weatherViewModel.loading.observeAsState(initial = false)
    val error by weatherViewModel.error.observeAsState(initial = null)

    // 現在の天気表示を削除し、日毎の天気予報のみを表示
    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.Center)
            )
        }
        
        error?.let { errorMsg ->
            Text(
                text = "エラー: $errorMsg", 
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // 天気予報データが存在する場合、DailyWeatherScreenを直接呼び出す
        weatherResponse?.daily?.let { dailyWeather ->
            // 入れ子のLazyColumnを避けるため、直接DailyWeatherScreenを呼び出す
            DailyWeatherScreen(dailyWeather = dailyWeather)
        }
    }
}
