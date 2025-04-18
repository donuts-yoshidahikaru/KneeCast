package com.tutorial.kneecast.data.remote

import android.util.Log
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository {

    companion object {
        private const val TAG = "WeatherRepository"
    }

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

    // メモリキャッシュの実装
    private val weatherCache = mutableMapOf<String, CachedWeatherInfo>()
    
    // キャッシュ有効期間（10分 = 10 * 60 * 1000ミリ秒）
    private val CACHE_EXPIRATION_MS = 10 * 60 * 1000L
    
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
                if (cachedInfo != null && (now - cachedInfo.timestamp) < CACHE_EXPIRATION_MS) {
                    Log.d(TAG, "キャッシュヒット: $cacheKey からデータを取得します")
                    return@withContext cachedInfo.weatherData
                }
                
                Log.d(TAG, "キャッシュミス: $cacheKey のデータを新しく取得します")
                
                // キャッシュがない場合は新しくデータを取得
                val coordinates = coords ?: getCoordinatesFromAddress(address) ?: return@withContext null
                val weatherResponse = getWeatherFromCoordinates(coordinates)
                
                // 取得に成功したらキャッシュに保存
                if (weatherResponse != null) {
                    Log.d(TAG, "キャッシュ保存: $cacheKey にデータを保存します")
                    weatherCache[cacheKey] = CachedWeatherInfo(weatherResponse, now)
                }
                
                weatherResponse
            } catch (e: Exception) {
                Log.e(TAG, "データ取得エラー", e)
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
    
    // キャッシュをクリアするメソッド（必要に応じて呼び出し）
    fun clearCache() {
        weatherCache.clear()
    }
    
    // 期限切れのキャッシュをクリアするメソッド
    fun clearExpiredCache() {
        val now = System.currentTimeMillis()
        val expiredKeys = weatherCache.entries
            .filter { (now - it.value.timestamp) >= CACHE_EXPIRATION_MS }
            .map { it.key }
        
        expiredKeys.forEach { weatherCache.remove(it) }
    }
}