package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tutorial.kneecast.ui.mapper.DailyWeatherUiModel

@Composable
fun DailyWeatherCard(
    uiModel: DailyWeatherUiModel,
    iconSize: Dp = 96.dp,
    fontSizeTemp: TextUnit = 20.sp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // ★ Box を使って重ね合わせ・位置指定を行う
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            /* ───── 左上：月日の表示 ───── */
            Text(
                text = run {
                    // "yyyy/MM/dd" → "M月d日" に整形
                    val dateParts = uiModel.displayDate.split("/")
                    if (dateParts.size >= 3) {
                        "${dateParts[1].toInt()}月${dateParts[2].toInt()}日"
                    } else {
                        uiModel.displayDate
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart)
            )

            /* ───── 中央：アイコン＋気温 ───── */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                // 天気アイコン
                Icon(
                    painter = painterResource(id = uiModel.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 最高 / 最低気温
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiModel.maxTempText,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = fontSizeTemp,
                        color = Color(0xFFFF6B00)   // オレンジ
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = fontSizeTemp
                    )
                    Text(
                        text = uiModel.minTempText,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = fontSizeTemp,
                        color = Color(0xFF0088FF)   // 青
                    )
                }
            }
        }
    }
}