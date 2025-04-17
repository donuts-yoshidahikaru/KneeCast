// com/tutorial/kneecast/ui/util/WeatherIconMapper.kt
package com.tutorial.kneecast.ui.util

import com.tutorial.kneecast.R

/**
 * 天気アイコンのコードをVector Drawableリソースにマッピング
 */
object WeatherIconMapper {
    fun mapIconCode(code: Int): Int = when (code) {
        1  -> R.drawable.ic_weather_not_available         // Not available
        2  -> R.drawable.ic_weather_sunny                 // Sunny
        3  -> R.drawable.ic_weather_mostly_sunny          // Mostly sunny
        4  -> R.drawable.ic_weather_partly_sunny          // Partly sunny
        5  -> R.drawable.ic_weather_mostly_cloudy         // Mostly cloudy
        6  -> R.drawable.ic_weather_cloudy                // Cloudy
        7  -> R.drawable.ic_weather_overcast              // Overcast
        8  -> R.drawable.ic_weather_low_clouds            // Overcast with low clouds
        9  -> R.drawable.ic_weather_fog                   // Fog
        10 -> R.drawable.ic_weather_light_rain            // Light rain
        11 -> R.drawable.ic_weather_rain                  // Rain
        12 -> R.drawable.ic_weather_possible_rain         // Possible rain
        13 -> R.drawable.ic_weather_rain_shower           // Rain shower
        14 -> R.drawable.ic_weather_thunderstorm          // Thunderstorm
        15 -> R.drawable.ic_weather_local_thunderstorms   // Local thunderstorms
        16 -> R.drawable.ic_weather_light_snow            // Light snow
        17 -> R.drawable.ic_weather_snow                  // Snow
        18 -> R.drawable.ic_weather_possible_snow         // Possible snow
        19 -> R.drawable.ic_weather_snow_shower           // Snow shower
        20 -> R.drawable.ic_weather_rain_and_snow         // Rain and snow
        21 -> R.drawable.ic_weather_possible_rain_and_snow// Possible rain and snow
        22 -> R.drawable.ic_weather_rain_and_snow         // Rain and snow
        23 -> R.drawable.ic_weather_freezing_rain         // Freezing rain
        24 -> R.drawable.ic_weather_possible_freezing_rain// Possible freezing rain
        25 -> R.drawable.ic_weather_hail                  // Hail
        26 -> R.drawable.ic_weather_clear_night           // Clear (night)
        27 -> R.drawable.ic_weather_mostly_clear_night    // Mostly clear (night)
        28 -> R.drawable.ic_weather_partly_clear_night    // Partly clear (night)
        29 -> R.drawable.ic_weather_mostly_cloudy_night   // Mostly cloudy (night)
        30 -> R.drawable.ic_weather_cloudy_night          // Cloudy (night)
        31 -> R.drawable.ic_weather_low_clouds_night      // Overcast with low clouds (night)
        32 -> R.drawable.ic_weather_rain_shower_night     // Rain shower (night)
        33 -> R.drawable.ic_weather_local_thunderstorms_night // Local thunderstorms (night)
        34 -> R.drawable.ic_weather_snow_shower_night     // Snow shower (night)
        35 -> R.drawable.ic_weather_rain_and_snow_night   // Rain and snow (night)
        36 -> R.drawable.ic_weather_possible_freezing_rain_night // Possible freezing rain (night)
        else -> R.drawable.ic_weather_not_available            // デフォルトアイコン
    }
}