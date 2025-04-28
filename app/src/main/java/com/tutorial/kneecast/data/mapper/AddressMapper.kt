package com.tutorial.kneecast.data.mapper

import com.tutorial.kneecast.data.local.entity.SavedAddress
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.model.Geometry

object AddressMapper {

    fun fromFeature(feature: Feature, isSelected: Boolean = false): SavedAddress {
        val (longitude, latitude) = feature.geometry.coordinates
            .split(",")
            .map { it.trim().toDouble() }
        
        return SavedAddress(
            addressName = feature.name,
            latitude = latitude,
            longitude = longitude,
            isSelected = isSelected
        )
    }

    fun toFeature(savedAddress: SavedAddress): Feature {
        return Feature(
            name = savedAddress.addressName,
            geometry = Geometry(
                coordinates = "${savedAddress.longitude},${savedAddress.latitude}"
            )
        )
    }

    fun toFeatures(savedAddresses: List<SavedAddress>): List<Feature> {
        return savedAddresses.map { toFeature(it) }
    }

//    fun fromFeatures(features: List<Feature>, isSelected: Boolean = false): List<SavedAddress> {
//        return features.map { fromFeature(it, isSelected) }
//    }
}