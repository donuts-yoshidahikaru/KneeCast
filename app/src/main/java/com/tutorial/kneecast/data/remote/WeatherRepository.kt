package com.tutorial.kneecast.data.remote

import android.util.Log
import com.tutorial.kneecast.data.mapper.WeatherResponseMapper
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.GoWeatherResponse
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class WeatherRepository {

    private val TAG = "Weather-Repository"

    // Yahoo!ジオコーダAPIのベースURL
    private val geocoderBaseUrl = "https://map.yahooapis.jp/"

    // Meteosource天気APIのベースURL - フォールバック用に残す
    private val weatherBaseUrl = "https://www.meteosource.com/"

    // GeocoderAPI - 住所から座標を取得するために使用
    private val geocoderApi: GeocoderApi = RetrofitFactory
        .createRetrofitInstance(geocoderBaseUrl)
        .create(GeocoderApi::class.java)

    // 既存のWeatherAPI - フォールバック用に残す
    private val weatherApi: WeatherApi = RetrofitFactory
        .createRetrofitInstance(weatherBaseUrl)
        .create(WeatherApi::class.java)

    // Goサーバー用のWeatherAPI
    private val weatherGoApi: WeatherGoApi = RetrofitFactory
        .createGoServerRetrofitInstance()
        .create(WeatherGoApi::class.java)

    // メモリキャッシュの実装
    private val weatherCache = mutableMapOf<String, CachedWeatherInfo>()
    
    // キャッシュ有効期間（10分 = 10 * 60 * 1000ミリ秒）
    private val cacheExpirationMs = 10 * 60 * 1000L
    
    // キャッシュデータを格納するデータクラス
    private data class CachedWeatherInfo(
        val weatherData: WeatherResponse,
        val timestamp: Long
    )
    
    // キャッシュキーを生成するヘルパーメソッド
    private fun generateCacheKey(address: String?, coords: Coordinates?): String {
        return when {
            coords != null -> "coords_${coords.latitude}_${coords.longitude}"
            address != null -> "address_$address"
            else -> throw IllegalArgumentException("住所または座標が必要です")
        }
    }

    suspend fun fetchWeatherInfo(address: String?, coords: Coordinates?): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // キャッシュキーを生成
                val cacheKey = generateCacheKey(address, coords)
                
                // キャッシュをチェック
                val cachedInfo = weatherCache[cacheKey]
                val now = System.currentTimeMillis()
                
                // キャッシュが有効な場合はキャッシュからデータを返す
                if (cachedInfo != null && (now - cachedInfo.timestamp) < cacheExpirationMs) {
                    // Timberの代わりにandroid.util.Logを使用
                    Timber.tag(TAG).i("キャッシュから天気情報を取得")
                    return@withContext cachedInfo.weatherData
                }

                // キャッシュがない場合は新しくデータを取得
                val coordinates = coords ?: getCoordinatesFromAddress(address) ?: return@withContext null
                
                // GoサーバーからWeather情報を取得
                var weatherResponse = getWeatherFromGoServer(coordinates)
                
                // Goサーバーからの取得に失敗した場合は外部APIにフォールバック
                if (weatherResponse == null) {
                    Timber.tag(TAG).w("Goサーバーからの取得に失敗、外部APIにフォールバック")
                    weatherResponse = getWeatherFromExternalApi(coordinates)
                }
                
                // 取得に成功したらキャッシュに保存
                if (weatherResponse != null) {
                    weatherCache[cacheKey] = CachedWeatherInfo(weatherResponse, now)
                }
                
                weatherResponse
            } catch (e: Exception) {
                Timber.tag(TAG).e("Weather fetch failed: ${e.message}")
                null
            }
        }
    }

    private suspend fun getCoordinatesFromAddress(address: String?): Coordinates? {
        if (address == null) return null
        
        try {
            // まずGoサーバーから座標を取得
            val geoResponse = geocoderApi.getCoordinatesFromGoServer(address = address)
            
            if (geoResponse.isSuccessful) {
                val goGeoResponseBody = geoResponse.body()
                if (goGeoResponseBody != null) {
                    Timber.tag(TAG).i("Goサーバーから座標を取得: $address")
                    return Coordinates(goGeoResponseBody.latitude, goGeoResponseBody.longitude)
                }
            }
            
            // Goサーバーからの取得に失敗した場合はYahoo APIから取得
            val yahooResponse = geocoderApi.getCoordinates(address = address)
            if (!yahooResponse.isSuccessful) return null

            val yahooResponseBody = yahooResponse.body() ?: return null
            val coordinatesStr = yahooResponseBody.feature[0].geometry.coordinates
            val parts = coordinatesStr.split(",")
            if (parts.size >= 2) {
                return Coordinates(parts[1].toDouble(), parts[0].toDouble())
            }
            return null
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "座標取得に失敗: $address")
            return null
        }
    }

    // Goサーバーから天気情報を取得
    private suspend fun getWeatherFromGoServer(coordinates: Coordinates): WeatherResponse? {
        try {
            Timber.tag(TAG)
                .i("Goサーバーから天気情報を取得: (${coordinates.latitude}, ${coordinates.longitude})")
            val response = weatherGoApi.getWeather(lat = coordinates.latitude, lon = coordinates.longitude)
            
            return if (response.isSuccessful) {
                val goWeatherResponse = response.body()
                if (goWeatherResponse != null) {
                    Timber.tag(TAG).i("Goサーバーからの応答成功: ${goWeatherResponse.locationName}")
                    // レスポンスをアプリのモデルに変換
                    WeatherResponseMapper.mapFromGoResponse(goWeatherResponse)
                } else {
                    Timber.tag(TAG).w("Goサーバーからのレスポンスが空")
                    null
                }
            } else {
                Timber.tag(TAG).w(
                    "Goサーバーからの応答失敗: ${response.code()} - ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Goサーバーからの天気情報取得に失敗")
            return null
        }
    }

    // 外部APIから天気情報を取得（フォールバック用）
    private suspend fun getWeatherFromExternalApi(coordinates: Coordinates): WeatherResponse? {
        try {
            Timber.tag(TAG)
                .i("外部APIから天気情報を取得: (${coordinates.latitude}, ${coordinates.longitude})")
            val weatherResponse = weatherApi.getWeather(
                lon = coordinates.longitude, 
                lat = coordinates.latitude
            )
            return if (weatherResponse.isSuccessful) {
                Timber.tag(TAG).i("外部APIからの応答成功")
                weatherResponse.body()
            } else {
                Timber.tag(TAG).w("外部APIからの応答失敗: ${weatherResponse.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "外部APIからの天気情報取得に失敗")
            return null
        }
    }
}