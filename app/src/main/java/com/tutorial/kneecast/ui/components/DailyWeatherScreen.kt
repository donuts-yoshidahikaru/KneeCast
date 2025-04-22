package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tutorial.kneecast.data.model.DailyWeather
import com.tutorial.kneecast.ui.mapper.toUiModel

@Composable
fun DailyWeatherScreen(
    dailyWeather: DailyWeather
) {
    val uiModels = dailyWeather.data.map { it.toUiModel() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(uiModels) { uiModel ->
            DailyWeatherCard(
                uiModel = uiModel,
                iconSize = 96.dp,
                fontSizeTemp = 24.sp
            )
        }
    }
}