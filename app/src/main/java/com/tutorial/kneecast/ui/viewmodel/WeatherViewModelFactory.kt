package com.tutorial.kneecast.ui.viewmodel
    
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * WeatherViewModelのファクトリクラス
 */
class WeatherViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
} 