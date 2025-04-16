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
    var address by remember { mutableStateOf("") }
    val weatherResponse by weatherViewModel.weatherResponse.observeAsState()
    val loading by weatherViewModel.loading.observeAsState(initial = false)
    val error by weatherViewModel.error.observeAsState(initial = null)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // ヘッダー部分：住所入力、ボタン、現在の天気、エラー表示など
        item {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("住所を入力") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { weatherViewModel.fetchWeatherInfo(address) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("天気を取得")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            error?.let { errorMsg ->
                Text(text = "エラー: $errorMsg", color = MaterialTheme.colorScheme.error)
            }
            weatherResponse?.let { weather ->
                Text(
                    text = "現在の天気: ${weather.current.summary}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "日次天気情報",
                    style = MaterialTheme.typography.titleMedium
                )
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
