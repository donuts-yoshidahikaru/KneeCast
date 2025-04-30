package com.tutorial.kneecast.data.mapper

import com.tutorial.kneecast.data.model.DailyData
import com.tutorial.kneecast.data.model.DailySubWeather
import com.tutorial.kneecast.data.model.DailyWeather
import com.tutorial.kneecast.data.model.GoWeatherResponse
import com.tutorial.kneecast.data.model.WeatherResponse

/**
 * GoサーバーのレスポンスをアプリのWeatherResponseモデルに変換するマッパー
 */
object WeatherResponseMapper {

    /**
     * GoWeatherResponseをアプリで使用するWeatherResponseに変換
     * 日付フォーマットの変換やデータ構造の調整を行う
     */
    fun mapFromGoResponse(goResponse: GoWeatherResponse): WeatherResponse {
        // デフォルトの天気アイコンID (晴れ=1, 曇り=3, 雨=4など)
        val weatherIcon = mapWeatherSummaryToIcon(goResponse.weatherSummary)
        
        // 日付フォーマットの変換（必要に応じて）
        val dayString = goResponse.targetDate // YYYY-MM-DD形式のまま使用

        // DailySubWeather（一日の天気情報）の作成
        val dailySubWeather = DailySubWeather(
            icon = weatherIcon,
            temperatureMin = goResponse.temperatureMin ?: 0.0,
            temperatureMax = goResponse.temperatureMax ?: 0.0
        )

        // DailyData（日ごとのデータ）の作成
        val dailyData = DailyData(
            day = dayString,
            allDay = dailySubWeather
        )

        // DailyWeatherの作成（リストで包む）
        val dailyWeather = DailyWeather(
            data = listOf(dailyData)
        )

        // 最終的なWeatherResponseの作成
        return WeatherResponse(
            daily = dailyWeather
        )
    }

    /**
     * 天気概要テキストをアイコンIDに変換
     */
    private fun mapWeatherSummaryToIcon(weatherSummary: String?): Int {
        return when {
            weatherSummary == null -> 1 // デフォルト：晴れ
            weatherSummary.contains("晴") -> 1
            weatherSummary.contains("曇") -> 3
            weatherSummary.contains("雨") -> 4
            weatherSummary.contains("雪") -> 5
            weatherSummary.contains("嵐") || weatherSummary.contains("雷") -> 8
            else -> 1 // デフォルト：晴れ
        }
    }
} 