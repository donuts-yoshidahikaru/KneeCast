package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutorial.kneecast.data.remote.GeocodeRepository // Import concrete GeocodeRepository
import com.tutorial.kneecast.data.remote.WeatherRepository // Import concrete WeatherRepository
import com.tutorial.kneecast.domain.usecase.GetCurrentWeatherUseCase

/**
 * WeatherViewModelのファクトリクラス
 */
class WeatherViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            // Instantiate dependencies for GetCurrentWeatherUseCase
            // This is a temporary direct instantiation. DI will handle this better in Step 9.
            val weatherRepository = WeatherRepository()
            val geocodeRepository = GeocodeRepository()
            val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository, geocodeRepository)
            
            return WeatherViewModel(getCurrentWeatherUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}