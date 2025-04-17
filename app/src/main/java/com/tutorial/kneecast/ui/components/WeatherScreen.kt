package com.tutorial.kneecast.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // ヘッダー部分：現在の天気、エラー表示など
        item {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
            }
            error?.let { errorMsg ->
                Text(
                    text = "エラー: $errorMsg", 
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            weatherResponse?.let { weather ->
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "現在の天気",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "天気: ${weather.current.summary}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "気温: ${weather.current.temperature}°C",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "風速: ${weather.current.wind.speed}m/s (${weather.current.wind.dir})",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "週間天気予報",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // daily 情報が存在する場合、その各項目を LazyColumn の items として追加
        weatherResponse?.daily?.let { dailyWeather ->
            items(dailyWeather.data) { dailyData ->
                DailyWeatherCard(dailyData = dailyData)
            }
        }
    }
}
