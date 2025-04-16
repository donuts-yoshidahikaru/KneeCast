package com.tutorial.kneecast.ui.components.currentLocationWeather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

class AddressWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()  // 横幅全体をカバー
                .background(Color(0xFFFFF9C4)),  // 薄い黄色の背景色
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Current Location View",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}
