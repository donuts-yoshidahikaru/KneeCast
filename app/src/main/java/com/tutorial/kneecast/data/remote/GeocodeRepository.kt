package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.data.model.GoGeocoderResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.model.Geometry
import com.tutorial.kneecast.data.model.ResultInfo
import java.net.ConnectException
import java.net.SocketTimeoutException
import retrofit2.HttpException
import timber.log.Timber

class GeocodeRepository {

    // Yahoo!ジオコーダAPIのベースURL
    private val geocoderBaseUrl = "https://map.yahooapis.jp/"
    private var geocoderApi: GeocoderApi = RetrofitFactory
        .createRetrofitInstance(geocoderBaseUrl)
        .create(GeocoderApi::class.java)
    
    // 新しいGoサーバー用のAPIインスタンス
    private var goGeocoderApi: GeocoderApi = RetrofitFactory
        .createGoServerRetrofitInstance()
        .create(GeocoderApi::class.java)

    /**
     * 指定された住所から座標情報を取得します。
     * 先にGoバックエンドに問い合わせを行い、失敗した場合はYahoo APIにフォールバックします。
     *
     * @param address 検索する住所
     * @return 座標情報を含むレスポンス、取得できない場合はnull
     */
    suspend fun getResponseFromAddress(address: String): GeocoderResponse? {
        return withContext(Dispatchers.IO) {
            if (address.isEmpty()) return@withContext null
            
            try {
                // Goバックエンドのエンドポイントを優先的に呼び出す
                Timber.tag("GeocodeRepository").d("Goバックエンドから座標を取得: address=$address")
                val geoResponse = goGeocoderApi.getCoordinatesFromGoServer(address = address)
                
                if (geoResponse.isSuccessful) {
                    val goGeoResponseBody = geoResponse.body()
                    if (goGeoResponseBody != null) {
                        Timber.tag("GeocodeRepository")
                            .d("Goバックエンドからの応答成功: ${goGeoResponseBody.name}")
                        // GeocoderResponseに変換
                        return@withContext convertToGeocoderResponse(goGeoResponseBody)
                    }
                } else {
                    Timber.tag("GeocodeRepository")
                        .w("Goバックエンドからの応答失敗: ${geoResponse.code()}")
                }
                
                // レスポンスが正常でなかった場合、Yahoo APIにフォールバック
                return@withContext fallbackToYahooApi(address)
            } catch (e: ConnectException) {
                // 接続エラー（サーバーが起動していない等）の場合
                Timber.tag("GeocodeRepository").w("Goサーバー接続エラー: ${e.message}")
                return@withContext fallbackToYahooApi(address)
            } catch (e: SocketTimeoutException) {
                // タイムアウトエラーの場合
                Timber.tag("GeocodeRepository").w("Goサーバータイムアウト: ${e.message}")
                return@withContext fallbackToYahooApi(address)
            } catch (e: HttpException) {
                // HTTPエラーの場合
                Timber.tag("GeocodeRepository").w("GoサーバーHTTPエラー: ${e.message}")
                return@withContext fallbackToYahooApi(address)
            } catch (e: Exception) {
                // その他の予期しないエラー
                Timber.tag("GeocodeRepository").e(e, "座標取得中に予期しないエラー発生")
                return@withContext fallbackToYahooApi(address)
            }
        }
    }
    
    /**
     * Yahoo APIにフォールバックして座標を取得します
     */
    private suspend fun fallbackToYahooApi(address: String): GeocoderResponse? {
        return try {
            Timber.tag("GeocodeRepository").d("Yahoo APIから座標を取得: address=$address")
            val yahooResponse = geocoderApi.getCoordinates(address = address)
            if (!yahooResponse.isSuccessful) {
                Timber.tag("GeocodeRepository")
                    .w("Yahoo APIからの応答失敗: ${yahooResponse.code()}")
                null
            } else {
                val yahooResponseBody = yahooResponse.body()
                if (yahooResponseBody != null) {
                    Timber.tag("GeocodeRepository").d("Yahoo APIからの応答成功")
                }
                yahooResponseBody
            }
        } catch (e: Exception) {
            Timber.tag("GeocodeRepository").e(e, "Yahoo API呼び出し中にエラー発生")
            null
        }
    }
    
    /**
     * Goサーバーレスポンスから元のGeocoderResponseへの変換
     * 
     * @param goResponse Goサーバーからのレスポンス
     * @return 変換されたGeocoderResponse
     */
    private fun convertToGeocoderResponse(goResponse: GoGeocoderResponse): GeocoderResponse {
        // 座標を「longitude,latitude」の形式の文字列に変換
        val coordinatesStr = "${goResponse.longitude},${goResponse.latitude}"
        
        // 既存のGeocoderResponseと互換性のある形式に変換
        return GeocoderResponse(
            resultInfo = ResultInfo(count = 1),
            feature = listOf(
                Feature(
                    geometry = Geometry(coordinates = coordinatesStr),
                    name = goResponse.name
                )
            )
        )
    }
} 