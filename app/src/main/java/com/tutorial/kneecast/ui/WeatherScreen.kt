package com.tutorial.kneecast.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tutorial.kneecast.data.model.WeatherResponse
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    // 住所入力用の状態変数
    var address by remember { mutableStateOf("") }
    
    // LiveData を Compose 側で監視（初期値を設定）
    val weatherResponse by weatherViewModel.weatherResponse.observeAsState()
    val loading by weatherViewModel.loading.observeAsState(initial = false)
    val error by weatherViewModel.error.observeAsState(initial = null)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // 住所入力欄
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("住所を入力") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 天気情報取得のボタン
        Button(
            onClick = { weatherViewModel.fetchWeatherInfo(address) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "天気を取得")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // ローディング中はプログレスインジケーターを表示
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        
        // エラーがある場合はエラーメッセージを表示
        error?.let { errorMsg ->
            Text(text = "エラー: $errorMsg", color = Color.Red)
        }
        
        // 天気情報が取得できている場合は情報を表示
        weatherResponse?.let { weather: WeatherResponse ->
            WeatherInfo(weather)
        }
    }
}

@Composable
fun WeatherInfo(weather: WeatherResponse) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "位置情報: ${weather.lat}, ${weather.lon}")
        Text(text = "現在の天気: ${weather.current.summary}")
        Text(text = "温度: ${weather.current.temperature} ${weather.units}")
        // 必要に応じて詳細情報（風速、雲量、降水量など）を追加表示できます。
        Spacer(modifier = Modifier.height(8.dp))
        // Hourly や Daily の情報もリスト表示にするなど拡張可能
    }
}