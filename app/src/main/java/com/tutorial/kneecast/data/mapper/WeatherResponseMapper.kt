package com.tutorial.kneecast.data.mapper

// Removed data model imports: DailyData, DailySubWeather, DailyWeather, WeatherResponse
import com.tutorial.kneecast.data.model.GoDailyData // Keep for input type
import com.tutorial.kneecast.data.model.GoWeatherResponse // Keep for input type
import com.tutorial.kneecast.domain.entity.DailyForecast // Added domain entity import
import com.tutorial.kneecast.domain.entity.WeatherInfo // Added domain entity import

/**
 * GoサーバーのレスポンスをドメインのWeatherInfoモデルに変換するマッパー
 */
object WeatherResponseMapper {

    /**
     * GoWeatherResponseをドメインで使用するWeatherInfoに変換
     * サーバーから返される7日間の天気予報データ、または単一日データを処理
     */
    fun mapFromGoResponse(goResponse: GoWeatherResponse): WeatherInfo {
        val dailyForecasts: List<DailyForecast>

        if (goResponse.daily != null) {
            // daily（7日間）のデータがある場合はそれを使用
            dailyForecasts = goResponse.daily.data.map { goDailyData ->
                mapGoDailyDataToDomainDailyForecast(goDailyData)
            }
        } else {
            // 後方互換性: dailyフィールドがない古い形式の場合、単一日のデータを作成
            val weatherIcon = mapWeatherSummaryToIcon(goResponse.weatherSummary)
            val singleForecast = DailyForecast(
                date = goResponse.targetDate, // Assuming targetDate is not null for old format
                iconId = weatherIcon,
                minTemp = goResponse.temperatureMin ?: 0.0,
                maxTemp = goResponse.temperatureMax ?: 0.0
                // Note: Other fields in DailyForecast might not be available from old format
            )
            dailyForecasts = listOf(singleForecast)
        }
        
        return WeatherInfo(
            locationName = goResponse.locationName, // Map locationName
            dailyForecasts = dailyForecasts
        )
    }
    
    /**
     * GoサーバーからのGoDailyDataをドメインのDailyForecastに変換
     */
    private fun mapGoDailyDataToDomainDailyForecast(goDailyData: GoDailyData): DailyForecast {
        return DailyForecast(
            date = goDailyData.day,
            iconId = goDailyData.allDay.icon,
            minTemp = goDailyData.allDay.temperatureMin,
            maxTemp = goDailyData.allDay.temperatureMax
            // Note: Other fields in DailyForecast might not be available or need default values
        )
    }

    /**
     * 天気概要テキストをアイコンIDに変換
     * (This function remains the same as provided in the problem description)
     */
    private fun mapWeatherSummaryToIcon(weatherSummary: String?): Int {
        return when {
            weatherSummary == null -> 1 // デフォルト：晴れ (Default: Sunny)
            weatherSummary.contains("晴") -> 1 // Sunny
            weatherSummary.contains("曇") -> 3 // Cloudy
            weatherSummary.contains("雨") -> 4 // Rain
            weatherSummary.contains("雪") -> 5 // Snow
            weatherSummary.contains("嵐") || weatherSummary.contains("雷") -> 8 // Storm or Thunder
            else -> 1 // デフォルト：晴れ (Default: Sunny)
        }
    }
}