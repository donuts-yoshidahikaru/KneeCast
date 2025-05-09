package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.data.model.GoGeocoderResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.model.Geometry
import com.tutorial.kneecast.data.model.ResultInfo
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.HttpException
import timber.log.Timber

class GeocodeRepository {

    // Goサーバー用のAPIインスタンス
    private var geocoderGoApi: GeocoderGoApi = RetrofitFactory
        .createGoServerRetrofitInstance()
        .create(GeocoderGoApi::class.java)

    /**
     * 指定された住所から座標情報を取得します。
     *
     * @param address 検索する住所
     * @return 座標情報を含むレスポンス、取得できない場合はnull
     */
    suspend fun getResponseFromAddress(address: String): GeocoderResponse? {
        return withContext(Dispatchers.IO) {
            if (address.isEmpty()) return@withContext null
            
            try {
                // Goバックエンドのエンドポイントを呼び出す
                Timber.tag("GeocodeRepository").d("Goバックエンドから座標を取得: address=$address")
                val geoResponse = geocoderGoApi.getCoordinatesFromGoServer(address = address)
                
                if (geoResponse.isSuccessful) {
                    val goGeoResponseBody = geoResponse.body()
                    if (goGeoResponseBody != null && goGeoResponseBody.locations.isNotEmpty()) {
                        Timber.tag("GeocodeRepository")
                            .d("Goバックエンドからの応答成功: ${goGeoResponseBody.locations.size}件の候補")
                        // GeocoderResponseに変換
                        return@withContext convertToGeocoderResponse(goGeoResponseBody)
                    } else {
                        Timber.tag("GeocodeRepository").w("Goバックエンドから位置情報が取得できませんでした")
                    }
                } else {
                    Timber.tag("GeocodeRepository")
                        .w("Goバックエンドからの応答失敗: ${geoResponse.code()}")
                }
                
                return@withContext null
            } catch (e: ConnectException) {
                // 接続エラー（サーバーが起動していない等）の場合
                Timber.tag("GeocodeRepository").w("Goサーバー接続エラー: ${e.message}")
                return@withContext null
            } catch (e: SocketTimeoutException) {
                // タイムアウトエラーの場合
                Timber.tag("GeocodeRepository").w("Goサーバータイムアウト: ${e.message}")
                return@withContext null
            } catch (e: HttpException) {
                // HTTPエラーの場合
                Timber.tag("GeocodeRepository").w("GoサーバーHTTPエラー: ${e.message}")
                return@withContext null
            } catch (e: Exception) {
                // その他の予期しないエラー
                Timber.tag("GeocodeRepository").e(e, "座標取得中に予期しないエラー発生")
                return@withContext null
            }
        }
    }
    
    /**
     * Goサーバーレスポンスから元のGeocoderResponseへの変換
     * 
     * @param goResponse Goサーバーからのレスポンス
     * @return 変換されたGeocoderResponse
     */
    private fun convertToGeocoderResponse(goResponse: GoGeocoderResponse): GeocoderResponse {
        // 各位置情報をFeatureに変換
        val features = goResponse.locations.map { location ->
            // 座標を「longitude,latitude」の形式の文字列に変換
            val coordinatesStr = "${location.longitude},${location.latitude}"
            
            Feature(
                geometry = Geometry(coordinates = coordinatesStr),
                name = location.name
            )
        }
        
        // 既存のGeocoderResponseと互換性のある形式に変換
        return GeocoderResponse(
            resultInfo = ResultInfo(count = features.size),
            feature = features
        )
    }
} 