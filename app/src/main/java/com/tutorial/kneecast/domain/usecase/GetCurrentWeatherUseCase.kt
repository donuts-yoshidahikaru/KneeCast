package com.tutorial.kneecast.domain.usecase

import com.tutorial.kneecast.domain.common.Result
import com.tutorial.kneecast.domain.entity.Coordinates
import com.tutorial.kneecast.domain.entity.WeatherInfo
import com.tutorial.kneecast.domain.repository.GeocodeRepository
import com.tutorial.kneecast.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(
    private val weatherRepository: WeatherRepository,
    private val geocodeRepository: GeocodeRepository
) {

    suspend fun execute(address: String?, currentCoords: Coordinates?): Result<WeatherInfo> {
        return try {
            val coordinatesToFetch: Coordinates? = if (currentCoords != null) {
                currentCoords
            } else if (!address.isNullOrBlank()) {
                when (val geocodeResult = geocodeRepository.getCoordinates(address)) {
                    is Result.Success -> geocodeResult.data
                    is Result.Error -> return Result.Error("Failed to get coordinates for address: ${address}. Error: ${geocodeResult.message}", geocodeResult.exception)
                }
            } else {
                // Neither address nor coordinates provided
                return Result.Error("Address or coordinates must be provided.", null)
            }

            if (coordinatesToFetch == null) {
                return Result.Error("Could not determine coordinates for weather lookup.", null)
            }

            // Now fetch weather using the determined coordinates
            weatherRepository.getWeather(coordinatesToFetch)

        } catch (e: Exception) {
            // Catch any other unexpected exceptions during the process
            Result.Error("An unexpected error occurred in GetCurrentWeatherUseCase: ${e.message}", e)
        }
    }
}
