package com.tutorial.kneecast.data.model

data class GeocoderResponse(
    val ResultInfo: ResultInfo,
    val Feature: List<Feature>
)

data class ResultInfo(
    val count: Int
)
data class Feature(
    val Geometry: Geometry,
    val Name: String
)

data class Geometry(
    val Coordinates: String
)