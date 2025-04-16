package com.tutorial.kneecast.ui.components.addressWeather

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
                .background(Color(0xFFE0F7FA)),  // 薄いシアンの背景色
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Address View",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}