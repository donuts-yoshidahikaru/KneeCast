package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.mapper.WeatherResponseMapper // Ensure this import is correct
import com.tutorial.kneecast.domain.entity.Coordinates
import com.tutorial.kneecast.domain.entity.WeatherInfo
import com.tutorial.kneecast.domain.repository.WeatherRepository as DomainWeatherRepository // Alias to avoid name clash
import com.tutorial.kneecast.domain.common.Result
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception // Explicit import for Result.Error

class WeatherRepository : DomainWeatherRepository { // Implement the domain interface

    private val tag = "Weather-Repository"

    // Goサーバー用のWeatherAPI
    private val weatherGoApi: WeatherGoApi = RetrofitFactory
        .createGoServerRetrofitInstance()
        .create(WeatherGoApi::class.java)

    // メモリキャッシュの実装
    private val weatherCache = mutableMapOf<String, CachedWeatherInfo>()
    
    // キャッシュ有効期間（10分 = 10 * 60 * 1000ミリ秒）
    private val cacheExpirationMs = 10 * 60 * 1000L
    
    // キャッシュデータを格納するデータクラス (updated to use domain entity)
    private data class CachedWeatherInfo(
        val weatherData: WeatherInfo, // Changed from data.model.WeatherResponse
        val timestamp: Long
    )
    
    // キャッシュキーを生成するヘルパーメソッド (updated for domain Coordinates)
    private fun generateCacheKey(coords: Coordinates): String {
        return "coords_${coords.latitude}_${coords.longitude}"
    }

    override suspend fun getWeather(coordinates: Coordinates): Result<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                // キャッシュキーを生成
                val cacheKey = generateCacheKey(coordinates)
                
                // キャッシュをチェック
                val cachedInfo = weatherCache[cacheKey]
                val now = System.currentTimeMillis()
                
                // キャッシュが有効な場合はキャッシュからデータを返す
                if (cachedInfo != null && (now - cachedInfo.timestamp) < cacheExpirationMs) {
                    Timber.tag(tag).i("キャッシュから天気情報を取得")
                    return@withContext Result.Success(cachedInfo.weatherData)
                }

                // キャッシュがない場合は新しくデータを取得
                // GoサーバーからWeather情報を取得 (updated to use domain Coordinates)
                val weatherResponseResult = getWeatherFromGoServer(coordinates) // This now returns Result<WeatherInfo>
                
                // 取得に成功したらキャッシュに保存
                if (weatherResponseResult is Result.Success) {
                    weatherCache[cacheKey] = CachedWeatherInfo(weatherResponseResult.data, now)
                    Timber.tag(tag).i("サーバーから取得した天気情報をキャッシュに保存")
                }
                
                weatherResponseResult // Return the Result (Success or Error)
            } catch (e: Exception) {
                Timber.tag(tag).e(e, "Weather fetch failed in getWeather")
                Result.Error("Weather fetch failed: ${e.message}", e)
            }
        }
    }

    // Goサーバーから天気情報を取得 (updated to use domain Coordinates and return Result<WeatherInfo>)
    private suspend fun getWeatherFromGoServer(coordinates: Coordinates): Result<WeatherInfo> {
        try {
            Timber.tag(tag)
                .i("Goサーバーから天気情報を取得: (${coordinates.latitude}, ${coordinates.longitude})")
            val response = weatherGoApi.getWeather(lat = coordinates.latitude, lon = coordinates.longitude)
            
            return if (response.isSuccessful) {
                val goWeatherResponse = response.body()
                if (goWeatherResponse != null) {
                    Timber.tag(tag).i("Goサーバーからの応答成功: ${goWeatherResponse.locationName}")
                    // Use WeatherResponseMapper to map to domain entity
                    val domainWeatherInfo = WeatherResponseMapper.mapFromGoResponse(goWeatherResponse)
                    Result.Success(domainWeatherInfo)
                } else {
                    Timber.tag(tag).w("Goサーバーからのレスポンスが空")
                    Result.Error("Go server response body is null")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Timber.tag(tag).w(
                    "Goサーバーからの応答失敗: ${response.code()} - $errorBody"
                )
                Result.Error("Go server request failed: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Goサーバーからの天気情報取得に失敗")
            return Result.Error("Failed to fetch weather from Go server: ${e.message}", e)
        }
    }
}