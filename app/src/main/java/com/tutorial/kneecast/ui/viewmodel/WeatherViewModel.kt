package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorial.kneecast.domain.entity.Coordinates
import com.tutorial.kneecast.domain.entity.WeatherInfo // Added import for domain WeatherInfo
import com.tutorial.kneecast.domain.usecase.GetCurrentWeatherUseCase
import com.tutorial.kneecast.domain.common.Result // Import Result for use case response
import kotlinx.coroutines.launch
// Removed import com.tutorial.kneecast.data.model.WeatherResponse
// Removed import com.tutorial.kneecast.data.remote.WeatherRepository


class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase // Added UseCase dependency
) : ViewModel() {

    // 天気情報取得中の状態を表す LiveData（オプションで UI にローディング表示を出すため）
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading // Expose loading state if needed by UI

    // 成功時の WeatherInfo を保持する LiveData (Type changed to domain entity)
    private val _weatherInfo = MutableLiveData<WeatherInfo?>()
    val weatherInfo: LiveData<WeatherInfo?> get() = _weatherInfo

    // エラーメッセージを保持する LiveData
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Removed: private val weatherRepository = WeatherRepository()

    fun fetchWeatherInfo(address: String?, coordinates: Coordinates?) {
        _loading.value = true
        // Clear previous error and data
        _error.postValue(null)
        _weatherInfo.postValue(null)

        viewModelScope.launch {
            try {
                // Call the use case
                when (val result = getCurrentWeatherUseCase.execute(address, coordinates)) {
                    is Result.Success -> {
                        _weatherInfo.postValue(result.data)
                        _error.postValue(null) // Clear error on success
                    }
                    is Result.Error -> {
                        _weatherInfo.postValue(null) // Clear data on error
                        _error.postValue(result.message ?: "An unknown error occurred.")
                    }
                }
            } catch (e: Exception) {
                // This catch block might be redundant if the use case handles all its exceptions
                // and wraps them in Result.Error. However, it's a safety net.
                _weatherInfo.postValue(null)
                _error.postValue("An unexpected exception occurred: ${e.localizedMessage}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}