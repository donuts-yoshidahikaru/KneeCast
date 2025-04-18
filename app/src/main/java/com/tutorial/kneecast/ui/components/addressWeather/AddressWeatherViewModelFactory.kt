package com.tutorial.kneecast.ui.components.addressWeather;

import androidx.lifecycle.ViewModelProvider
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel

class AddressWeatherViewModelFactory : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
