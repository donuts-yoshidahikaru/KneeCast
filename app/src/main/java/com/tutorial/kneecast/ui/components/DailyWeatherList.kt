package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.tutorial.kneecast.data.model.DailyWeather

@Composable
fun DailyWeatherList(dailyWeather: DailyWeather) {
    LazyColumn {
        // dailyWeather.data の各要素を表示
        items(dailyWeather.data) { dailyData ->
            DailyWeatherCard(dailyData = dailyData)
        }
    }
}