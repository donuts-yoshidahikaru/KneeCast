package com.tutorial.kneecast.data.mapper

import com.tutorial.kneecast.data.model.DailyData
import com.tutorial.kneecast.data.model.DailySubWeather
import com.tutorial.kneecast.data.model.DailyWeather
import com.tutorial.kneecast.data.model.GoDailyData
import com.tutorial.kneecast.data.model.GoWeatherResponse
import com.tutorial.kneecast.data.model.WeatherResponse

/**
 * GoサーバーのレスポンスをアプリのWeatherResponseモデルに変換するマッパー
 */
object WeatherResponseMapper {

    /**
     * GoWeatherResponseをアプリで使用するWeatherResponseに変換
     * サーバーから返される7日間の天気予報データを処理
     */
    fun mapFromGoResponse(goResponse: GoWeatherResponse): WeatherResponse {
        // daily（7日間）のデータがある場合はそれを使用
        if (goResponse.daily != null) {
            // サーバーからの7日間データを変換
            val dailyDataList = goResponse.daily.data.map { goDailyData ->
                mapGoDailyDataToDailyData(goDailyData)
            }
            
            return WeatherResponse(
                daily = DailyWeather(data = dailyDataList)
            )
        }
        
        // 以下は後方互換性のために残す（dailyフィールドがない古い形式の場合）
        // 単一日のデータを作成（古い形式のサーバーレスポンス用）
        val weatherIcon = mapWeatherSummaryToIcon(goResponse.weatherSummary)
        val dayString = goResponse.targetDate
        
        val dailySubWeather = DailySubWeather(
            icon = weatherIcon,
            temperatureMin = goResponse.temperatureMin ?: 0.0,
            temperatureMax = goResponse.temperatureMax ?: 0.0
        )
        
        val dailyData = DailyData(
            day = dayString,
            allDay = dailySubWeather
        )
        
        return WeatherResponse(
            daily = DailyWeather(data = listOf(dailyData))
        )
    }
    
    /**
     * GoサーバーからのDailyDataをアプリのDailyDataに変換
     */
    private fun mapGoDailyDataToDailyData(goDailyData: GoDailyData): DailyData {
        return DailyData(
            day = goDailyData.day,
            allDay = DailySubWeather(
                icon = goDailyData.allDay.icon,
                temperatureMin = goDailyData.allDay.temperatureMin,
                temperatureMax = goDailyData.allDay.temperatureMax
            )
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