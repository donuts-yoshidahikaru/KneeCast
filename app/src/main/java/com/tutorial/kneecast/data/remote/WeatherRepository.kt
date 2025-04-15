package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository {

    // ベースURLはYahoo! APIのエンドポイントの共通部分を指定
    private val baseUrl = "https://map.yahooapis.jp/"

    private val geocoderApi: GeocoderApi by lazy {
        RetrofitFactory.createRetrofitInstance(baseUrl).create(GeocoderApi::class.java)
    }

    private val weatherApi: WeatherApi by lazy {
        RetrofitFactory.createRetrofitInstance(baseUrl).create(WeatherApi::class.java)
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
        val weatherResponse = weatherApi.getWeather(coordinates = coordinates)
        return if (weatherResponse.isSuccessful) weatherResponse.body() else null
    }
}