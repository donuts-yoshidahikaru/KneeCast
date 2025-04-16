package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository {

    // Yahoo!ジオコーダAPIのベースURL
    private val geocoderBaseUrl = "https://map.yahooapis.jp/"

    // Meteosource天気APIのベースURL
    private val weatherBaseUrl = "https://www.meteosource.com/"

    private val geocoderApi: GeocoderApi by lazy {
        RetrofitFactory.createRetrofitInstance(geocoderBaseUrl).create(GeocoderApi::class.java)
    }

    private val weatherApi: WeatherApi by lazy {
        RetrofitFactory.createRetrofitInstance(weatherBaseUrl).create(WeatherApi::class.java)
    }

    suspend fun fetchWeatherInfo(address: String): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. ジオコーダAPI：住所から緯度・経度を取得
                val coordinates = getCoordinatesFromAddress(address) ?: return@withContext null

                // 2. 気象情報API：取得した座標から天気情報を取得
                getWeatherFromCoordinates(coordinates)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun getCoordinatesFromAddress(address: String): String? {
        val geoResponse = geocoderApi.getCoordinates(address = address)
        if (!geoResponse.isSuccessful) return null

        val geoResponseBody = geoResponse.body() ?: return null
        return geoResponseBody.Feature[0].Geometry.Coordinates
    }

    private suspend fun getWeatherFromCoordinates(coordinates: String): WeatherResponse? {
        val (lonStr, latStr) = coordinates.split(",")
        val lon: Double = lonStr.toDouble()
        val lat: Double = latStr.toDouble()
        val weatherResponse = weatherApi.getWeather(lon = lon, lat = lat)
        return if (weatherResponse.isSuccessful) weatherResponse.body() else null
    }
}