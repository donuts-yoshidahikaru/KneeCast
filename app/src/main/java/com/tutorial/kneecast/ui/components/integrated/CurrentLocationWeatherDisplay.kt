package com.tutorial.kneecast.ui.components.integrated

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.ui.components.WeatherScreen
import com.tutorial.kneecast.ui.viewmodel.LocationViewModel
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel
import com.tutorial.kneecast.ui.viewmodel.provideWeatherViewModel
import timber.log.Timber

/**
 * 現在地の天気情報を表示するコンポーネント
 */
@Composable
fun CurrentLocationWeatherDisplay() {
    // LocationViewModelとWeatherViewModelを取得
    val locationViewModel = provideLocationViewModel()
    val weatherViewModel = provideWeatherViewModel()
    
    // LiveDataをStateとして観測
    val location by locationViewModel.location.observeAsState()
    val isLoading by locationViewModel.loading.observeAsState(false)
    val error by locationViewModel.error.observeAsState()
    
    // 天気情報の状態
    val weatherLoading by weatherViewModel.loading.observeAsState(initial = false)
    val weatherError by weatherViewModel.error.observeAsState(initial = null)
    
    // 画面表示時に一度だけ位置情報を自動的に取得
    LaunchedEffect(Unit) {
        Timber.d("現在地の天気情報表示を初期化")
        locationViewModel.fetchLocation()
    }
    
    // 位置情報が変更されたときに天気情報を取得
    LaunchedEffect(location) {
        location?.let {
            Timber.d("現在地の位置情報が更新されました: ${it.latitude}, ${it.longitude}")
            val coordinates = Coordinates(it.latitude, it.longitude)
            weatherViewModel.fetchWeatherInfo("現在地", coordinates)
        }
    }
    
    // 読み込み中
    if (isLoading || weatherLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    
    // 位置情報のエラー
    if (error != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "位置情報エラー: $error",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    locationViewModel.clearError()
                    locationViewModel.fetchLocation()
                }
            ) {
                Text("再試行")
            }
        }
        return
    }
    
    // 位置情報が取得できていない場合
    if (location == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "位置情報を取得中...",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    locationViewModel.fetchLocation()
                }
            ) {
                Text("位置情報を取得")
            }
        }
        return
    }
    
    // 天気情報のエラー
    if (weatherError != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "天気情報エラー: $weatherError",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    location?.let {
                        val coordinates = Coordinates(it.latitude, it.longitude)
                        weatherViewModel.fetchWeatherInfo("現在地", coordinates)
                    }
                }
            ) {
                Text("再試行")
            }
        }
        return
    }
    
    // 天気情報の表示
    WeatherScreen(weatherViewModel = weatherViewModel)
} 