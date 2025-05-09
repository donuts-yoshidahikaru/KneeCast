package com.tutorial.kneecast.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.tutorial.kneecast.data.model.GoGeocoderResponse

/**
 * ジオコーディングAPIインターフェース
 */
interface GeocoderGoApi {
    /**
     * 住所から座標を取得するGoサーバーエンドポイント
     */
    @GET("geocode")
    suspend fun getCoordinatesFromGoServer(
        @Query("address") address: String
    ): Response<GoGeocoderResponse>
}