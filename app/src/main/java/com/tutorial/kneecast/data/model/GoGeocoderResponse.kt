package com.tutorial.kneecast.data.model

import com.google.gson.annotations.SerializedName

/**
 * Goバックエンドの位置情報を表現するデータクラス
 * 
 * @property name 地点名
 * @property latitude 緯度
 * @property longitude 経度
 * @property address 住所
 */
data class GoLocation(
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String
)

/**
 * Goバックエンドの/geocodeエンドポイントから返されるレスポンスを表現するデータクラス
 * 
 * @property locations 検索結果の位置情報リスト
 */
data class GoGeocoderResponse(
    @SerializedName("locations") val locations: List<GoLocation>
) 