package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tutorial.kneecast.data.model.Feature
import timber.log.Timber

/**
 * 候補住所のリストアイテム
 */
@Composable
fun SuggestionItem(suggestion: Feature, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "住所",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = suggestion.Name,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * 選択された住所のリストアイテム
 */
@Composable
fun SelectedAddressItem(
    address: Feature,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    // ログを追加して状態を確認
    Timber.tag("SelectedAddressItem").d("Address: ${address.Name}, isSelected: $isSelected")
    
    // 選択状態に応じた色とスタイルを設定 (remember を削除)
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                          else MaterialTheme.colorScheme.surfaceVariant
    
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
    
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
                      else Color.Transparent
    
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = address.Name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "削除",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
} 