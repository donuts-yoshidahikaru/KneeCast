package com.tutorial.kneecast.data.mapper

import com.tutorial.kneecast.data.model.GoGeocoderResponse
import com.tutorial.kneecast.domain.entity.Coordinates

object GeocodeResponseMapper {

    fun mapToDomainCoordinates(goResponse: GoGeocoderResponse): Coordinates? {
        // If multiple results are possible, the strategy is to pick the first one.
        // If no locations, return null, repository can handle this as an error.
        if (goResponse.locations.isEmpty()) {
            return null
        }
        val firstLocation = goResponse.locations.first()
        return Coordinates(
            latitude = firstLocation.latitude,
            longitude = firstLocation.longitude
        )
    }
}
