package com.tutorial.kneecast.ui.components.currentLocationWeather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.R
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.ui.components.WeatherScreen
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel
import com.tutorial.kneecast.ui.viewmodel.provideWeatherViewModel

class CurrentLocationWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LocationWeatherViewer(modifier = Modifier)
        }
    }
    
    @Composable
    fun LocationWeatherViewer(modifier: Modifier = Modifier) {
        // LocationViewModelとWeatherViewModelを取得
        val locationViewModel = provideLocationViewModel()
        val weatherViewModel = provideWeatherViewModel()
        
        // LiveDataをStateとして観測
        val location by locationViewModel.location.observeAsState()
        val isLoading by locationViewModel.loading.observeAsState(false)
        val error by locationViewModel.error.observeAsState()
        
        // 画面表示時に一度だけ自動的に位置情報を取得
        LaunchedEffect(Unit) {
            locationViewModel.fetchLocation()
        }
        
        // 位置情報が変更されたときに天気情報を取得
        LaunchedEffect(location) {
            location?.let {
                val coordinates = Coordinates(it.latitude, it.longitude)
                weatherViewModel.fetchWeatherInfo(null, coordinates)
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 位置情報取得ボタンと状態表示
            Button(
                onClick = {
                    // エラーをクリアして位置情報を取得
                    locationViewModel.clearError()
                    locationViewModel.fetchLocation()
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "現在地の天気を取得",
                )
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            // 天気情報の表示（位置情報が取得できている場合のみ）
            if (location != null) {
                if (error != null) {
                    // エラーがある場合はエラーアイコンを表示
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = "エラー",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                } else {
                    WeatherScreen(weatherViewModel = weatherViewModel)
                }
            }
        }
    }
}
