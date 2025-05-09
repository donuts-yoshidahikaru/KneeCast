package com.tutorial.kneecast.ui.components.integrated

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 現在地を表示するリストアイテム
 */
@Composable
fun CurrentLocationItem(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 選択状態に応じた色とスタイルを設定
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                          else MaterialTheme.colorScheme.surfaceVariant
    
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
    
    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = Modifier
            .wrapContentWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Box(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = "現在地",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
} 