package com.tutorial.kneecast.data.model

data class GeocoderResponse(
    val Feature: List<Feature>
)

data class Feature(
    val Geometry: Geometry
)

data class Geometry(
    val Coordinates: String
)