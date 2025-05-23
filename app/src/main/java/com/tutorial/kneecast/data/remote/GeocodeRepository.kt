package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GoGeocoderResponse // Keep this for network call response type
import com.tutorial.kneecast.domain.entity.Coordinates // Import domain entity
import com.tutorial.kneecast.domain.repository.GeocodeRepository as DomainGeocodeRepository // Alias
import com.tutorial.kneecast.domain.common.Result // Import domain Result
import com.tutorial.kneecast.network.RetrofitFactory
import com.tutorial.kneecast.data.mapper.GeocodeResponseMapper // New import
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Exception // Explicit import for Result.Error

class GeocodeRepository : DomainGeocodeRepository { // Implement the domain interface

    private val tag = "Geocode-Repository"

    // Goサーバー用のAPIインスタンス
    private var geocoderGoApi: GeocoderGoApi = RetrofitFactory
        .createGoServerRetrofitInstance()
        .create(GeocoderGoApi::class.java)

    /**
     * 指定された住所から座標情報を取得します。
     *
     * @param address 検索する住所
     * @return Result holding Coordinates or an error.
     */
    override suspend fun getCoordinates(address: String): Result<Coordinates> {
        return withContext(Dispatchers.IO) {
            if (address.isEmpty()) {
                return@withContext Result.Error("Address cannot be empty.")
            }
            
            try {
                // Goバックエンドのエンドポイントを呼び出す
                Timber.tag(tag).d("Goバックエンドから座標を取得: address=$address")
                val geoResponse = geocoderGoApi.getCoordinatesFromGoServer(address = address)
                
                if (geoResponse.isSuccessful) {
                    val goGeoResponseBody = geoResponse.body()
                    if (goGeoResponseBody != null) { 
                        val domainCoordinates = GeocodeResponseMapper.mapToDomainCoordinates(goGeoResponseBody)
                        if (domainCoordinates != null) {
                            Timber.tag(tag)
                                .d("Goバックエンドからの応答成功: ${goGeoResponseBody.locations.size}件の候補") // Log original count for info
                            return@withContext Result.Success(domainCoordinates)
                        } else {
                            Timber.tag(tag).w("GeocodeResponseMapper returned null (no locations)")
                            return@withContext Result.Error("No locations found for the address.")
                        }
                    } else {
                         Timber.tag(tag).w("Goバックエンドから位置情報が取得できませんでした or empty body")
                        return@withContext Result.Error("No locations found for the address (empty body).")
                    }
                } else {
                    val errorBody = geoResponse.errorBody()?.string() ?: "Unknown error"
                    Timber.tag(tag)
                        .w("Goバックエンドからの応答失敗: ${geoResponse.code()} - $errorBody")
                    return@withContext Result.Error("Geocoding service request failed: ${geoResponse.code()} - $errorBody")
                }
            } catch (e: ConnectException) {
                Timber.tag(tag).w(e, "Goサーバー接続エラー")
                return@withContext Result.Error("Connection error to geocoding service: ${e.message}", e)
            } catch (e: SocketTimeoutException) {
                Timber.tag(tag).w(e, "Goサーバータイムアウト")
                return@withContext Result.Error("Geocoding service timeout: ${e.message}", e)
            } catch (e: HttpException) {
                Timber.tag(tag).w(e, "GoサーバーHTTPエラー")
                return@withContext Result.Error("Geocoding service HTTP error: ${e.message}", e)
            } catch (e: Exception) {
                Timber.tag(tag).e(e, "座標取得中に予期しないエラー発生")
                return@withContext Result.Error("Unexpected error during geocoding: ${e.message}", e)
            }
        }
    }
    // Removed private fun convertToDomainCoordinates(goResponse: GoGeocoderResponse)
}