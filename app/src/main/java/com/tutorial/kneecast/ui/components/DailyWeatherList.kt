package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.tutorial.kneecast.ui.mapper.DailyWeatherUiModel

/**
 * 汎用的なリストコンポーネント
 */
@Composable
fun DailyWeatherList(
    items: List<DailyWeatherUiModel>,
    iconSize: Dp,
    fontSizeTemp: TextUnit
) {
    LazyColumn {
        items(items) { uiModel ->
            DailyWeatherCard(
                uiModel = uiModel,
                iconSize = iconSize,
                fontSizeTemp = fontSizeTemp
            )
        }
    }
}