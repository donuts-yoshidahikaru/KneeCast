package com.tutorial.kneecast.data.model

import com.google.gson.annotations.SerializedName

/**
 * Goバックエンドの/geocodeエンドポイントから返されるレスポンスを表現するデータクラス
 * 
 * @property name 地点名
 * @property latitude 緯度
 * @property longitude 経度
 */
data class GoGeocoderResponse(
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
) 