package com.tutorial.kneecast.ui.components.currentLocationWeather

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel

class CurrentLocationWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()  // 横幅全体をカバー
                .background(Color(0xFFFFF9C4)),  // 薄い黄色の背景色
            contentAlignment = Alignment.Center
        ) {
            LocationViewer(modifier = Modifier)
        }
    }
    
    @Composable
    fun LocationViewer(modifier: Modifier = Modifier) {
        // LocationViewModelを取得（自動的に初期化されます）
        val viewModel = provideLocationViewModel()
        
        // LiveDataをStateとして観測
        val location by viewModel.location.observeAsState()
        val isLoading by viewModel.loading.observeAsState(false)
        val error by viewModel.error.observeAsState()
        
        // 位置情報を表示するための変数
        val latitude = location?.latitude?.toString() ?: ""
        val longitude = location?.longitude?.toString() ?: ""
        
        // 状態メッセージの生成
        val statusMessage = when {
            error != null -> "エラー: $error"
            isLoading -> "位置情報を取得中..."
            location != null -> "位置情報を取得しました"
            else -> "位置情報を取得してください"
        }
        
        // 画面表示時に一度だけ自動的に位置情報を取得
        LaunchedEffect(Unit) {
            viewModel.fetchLocation()
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "緯度:$latitude\n経度:$longitude",
                modifier = modifier
            )
            
            Text(
                text = statusMessage,
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (error != null) Color.Red else Color.Black
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Button(
                onClick = {
                    // エラーをクリアして位置情報を取得
                    viewModel.clearError()
                    viewModel.fetchLocation()
                },
                enabled = !isLoading
            ) {
                Text(
                    text = "位置情報取得",
                )
            }
        }
    }
}
