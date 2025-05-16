package com.tutorial.kneecast.data.mapper

import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.Feature

fun Feature.toCoordinates(): Coordinates? {
    val (lngStr, latStr) = geometry.coordinates.split(",").map { it.trim() } + listOf("", "")
    val lng = lngStr.toDoubleOrNull()
    val lat = latStr.toDoubleOrNull()
    return if (lng != null && lat != null) Coordinates(latitude = lat, longitude = lng) else null
}