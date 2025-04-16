package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.data.model.DailyData

@Composable
fun DailyWeatherCard(dailyData: DailyData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "日付: ${dailyData.day}", style = MaterialTheme.typography.titleMedium)
            Text(text = "天気: ${dailyData.weather}")
            Text(text = "概要: ${dailyData.summary}")
            Text(text = "温度: ${dailyData.all_day.temperature} (${dailyData.all_day.temperature_min}〜${dailyData.all_day.temperature_max})")
        }
    }
}
