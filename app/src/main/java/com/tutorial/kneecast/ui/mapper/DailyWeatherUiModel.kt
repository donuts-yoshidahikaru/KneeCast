package com.tutorial.kneecast.ui.mapper

import com.tutorial.kneecast.data.model.DailyData
import com.tutorial.kneecast.ui.unit.DateFormatter
import com.tutorial.kneecast.ui.unit.WeatherIconMapper

/**
 * UI表示用のデータモデル
 */
data class DailyWeatherUiModel(
    val displayDate: String,
    val iconRes: Int,
    val maxTempText: String,
    val minTempText: String
)

/**
 * ドメインモデル -> UIモデル変換拡張
 */
fun DailyData.toUiModel(): DailyWeatherUiModel {
    val date = DateFormatter.format(day)
    val icon = WeatherIconMapper.mapIconCode(allDay.icon)
    val maxText = "${allDay.temperatureMax}°C"
    val minText = "${allDay.temperatureMin}°C"

    return DailyWeatherUiModel(
        displayDate = date,
        iconRes = icon,
        maxTempText = maxText,
        minTempText = minText
    )
}