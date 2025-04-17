package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.Coordinates
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

    suspend fun fetchWeatherInfo(address: String?, coords: Coordinates?): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val coordinates = coords ?: getCoordinatesFromAddress(address) ?: return@withContext null
                getWeatherFromCoordinates(coordinates)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun getCoordinatesFromAddress(address: String?): Coordinates? {
        if (address == null) return null
        val geoResponse = geocoderApi.getCoordinates(address = address)
        if (!geoResponse.isSuccessful) return null

        val geoResponseBody = geoResponse.body() ?: return null
        val coordinates: String = geoResponseBody.Feature[0].Geometry.Coordinates
        return Coordinates(coordinates[0].code.toDouble(), coordinates[1].code.toDouble())
    }

    private suspend fun getWeatherFromCoordinates(coordinates: Coordinates): WeatherResponse? {
        val weatherResponse = weatherApi.getWeather(lon = coordinates.longitude, lat = coordinates.latitude)
        return if (weatherResponse.isSuccessful) weatherResponse.body() else null
    }
}