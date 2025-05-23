package com.tutorial.kneecast.domain.repository

import com.tutorial.kneecast.domain.common.Result
import com.tutorial.kneecast.domain.entity.Coordinates
import com.tutorial.kneecast.domain.entity.WeatherInfo

interface WeatherRepository {
    suspend fun getWeather(coordinates: Coordinates): Result<WeatherInfo>
}
