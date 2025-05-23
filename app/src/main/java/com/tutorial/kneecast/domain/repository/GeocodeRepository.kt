package com.tutorial.kneecast.domain.repository

import com.tutorial.kneecast.domain.common.Result
import com.tutorial.kneecast.domain.entity.Coordinates

interface GeocodeRepository {
    suspend fun getCoordinates(address: String): Result<Coordinates>
}
