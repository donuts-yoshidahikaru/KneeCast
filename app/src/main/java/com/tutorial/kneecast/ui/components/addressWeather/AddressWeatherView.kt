package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AddressWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F7FA))  // 薄いシアンの背景色
        ) {
            // 住所入力部分
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val address = remember { mutableStateOf("") }
                
                TextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("住所を入力") }
                )
                
                Button(
                    onClick = { /* ここに追加処理を実装 */ }
                ) {
                    Text("追加する")
                }
            }
            
            // 天気表示部分
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
}