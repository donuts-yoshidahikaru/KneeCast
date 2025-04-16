package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.data.remote.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WeatherViewModel : ViewModel() {

    // 天気情報取得中の状態を表す LiveData（オプションで UI にローディング表示を出すため）
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // 成功時の WeatherResponse を保持する LiveData
    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> get() = _weatherResponse

    // エラーメッセージを保持する LiveData
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // WeatherRepository のインスタンス（DI などを利用する場合はそれに合わせてもよい）
    private val weatherRepository = WeatherRepository()

    fun fetchWeatherInfo(address: String) {
        _loading.value = true

        viewModelScope.launch {
            try {
                val response = weatherRepository.fetchWeatherInfo(address)
                // Repository の結果が null でなければ成功として UI に通知
                if (response != null) {
                    _weatherResponse.postValue(response)
                    _error.postValue(null)
                } else {
                    // 結果が null の場合はエラーとして扱う
                    _error.postValue("天気情報が取得できませんでした。")
                }
            } catch (e: Exception) {
                _error.postValue("例外発生: ${e.localizedMessage}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}