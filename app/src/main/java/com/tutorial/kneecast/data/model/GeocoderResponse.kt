package com.tutorial.kneecast.data.model

import com.google.gson.annotations.SerializedName

//@Suppress("UNUSED_PARAMETER")
data class GeocoderResponse(
    @SerializedName("ResultInfo") val resultInfo: ResultInfo,
    @SerializedName("Feature") val feature: List<Feature>
)

//@Suppress("unused")
data class ResultInfo(
    @SerializedName("count") val count: Int
)

data class Feature(
    @SerializedName("Geometry") val geometry: Geometry,
    @SerializedName("Name") val name: String
)

data class Geometry(
    @SerializedName("Coordinates") val coordinates: String
)