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
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.location.GPSLocationManager
import com.tutorial.kneecast.ui.LocalAppContext


class CurrentLocationWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        // LocalAppContextからContextを取得
        val context = LocalAppContext.current
        val gpsLocationManager = GPSLocationManager(context)

        Box(
                modifier = modifier
                        .fillMaxWidth()  // 横幅全体をカバー
                        .background(Color(0xFFFFF9C4)),  // 薄い黄色の背景色
                contentAlignment = Alignment.Center
        ) {
            GPSViewer(
                modifier = Modifier,
                gpsLocationManager
            )
        }
    }
    @Composable
    fun GPSViewer(modifier: Modifier = Modifier, gpsLocationManager: GPSLocationManager) {
        // LocalAppContextからContextを取得
        val context = LocalAppContext.current
        
        var latitude by remember { mutableStateOf("") }
        var longitude by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(true) } // 初期状態をロード中に変更
        var statusMessage by remember { mutableStateOf("位置情報を取得中...") } // 初期メッセージを変更
        
        // 位置情報コールバックを作成
        val locationCallback = remember {
            object : GPSLocationManager.MyLocationCallback {
                override fun onLocationResult(location: Location?) {
                    isLoading = false
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        statusMessage = "位置情報を取得しました"
                    }
                }

                override fun onLocationError(error: String) {
                    isLoading = false
                    latitude = "取得失敗"
                    longitude = "取得失敗"
                    statusMessage = "エラー: $error"
                }
            }
        }
        
        // 画面表示時に一度だけ自動的に位置情報を取得
        LaunchedEffect(Unit) {
            gpsLocationManager.getLastLocation(locationCallback)
        }
        
        Column (modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "緯度:$latitude\n経度:$longitude",
                modifier = modifier
            )
            
            Text(
                text = statusMessage,
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (statusMessage.contains("エラー")) Color.Red else Color.Black
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Button(
                onClick = {
                    isLoading = true
                    statusMessage = "位置情報を取得中..."
                    gpsLocationManager.getLastLocation(locationCallback)
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
