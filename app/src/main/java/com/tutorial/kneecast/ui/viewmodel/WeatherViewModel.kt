package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tutorial.kneecast.data.remote.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()
    private val _weatherInfo = MutableStateFlow("Loading...")
    val weatherInfo: StateFlow<String> = _weatherInfo

    init {
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        viewModelScope.launch {
            val result = repository.fetchWeatherInfo("東京都新宿区")
            result?.let {
                _weatherInfo.value = Gson().toJson(it)
            } ?: run {
                _weatherInfo.value = "Error fetching weather data."
            }
        }
    }
}