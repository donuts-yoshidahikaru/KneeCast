package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherView
import com.tutorial.kneecast.ui.components.currentLocationWeather.CurrentLocationWeatherView

class MainScreen {
    @Composable
    fun Content() {
        // 各 View のクラスをインスタンス化
        val addressView = AddressWeatherView()
        val currentLocationView = CurrentLocationWeatherView()

        // Column で上下に並べる。weight(1f) により、それぞれが均等（画面の半分）を占有します。
        Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            addressView.Content(
                    modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .weight(1f)
            )
            currentLocationView.Content(
                    modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .weight(1f)
            )
        }
    }
}
