package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.ui.viewmodel.AddressWeatherViewModel
import android.util.Log
import android.widget.Toast
import timber.log.Timber

/**
 * 住所検索と天気表示のメイン画面
 */
class AddressWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val viewModel: AddressWeatherViewModel = viewModel()
        val addressInput by viewModel.addressInput.collectAsState()
        val suggestions by viewModel.addressSuggestions.collectAsState()
        val selectedAddresses by viewModel.selectedAddresses.collectAsState()
        val currentSelectedAddress by viewModel.currentSelectedAddress.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        
        // UI側で選択中の住所を追跡（デバッグ用）
        var selectedAddressName by remember { mutableStateOf("") }
        
        // 現在選択中の住所が変わったらローカル状態も更新
        LaunchedEffect(currentSelectedAddress) {
            currentSelectedAddress?.let {
                selectedAddressName = it.Name
                Timber.tag("AddressWeatherView").d("現在選択中の住所: ${it.Name}")
            }
        }
        
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 住所入力フィールド
            OutlinedTextField(
                value = addressInput,
                onValueChange = { viewModel.updateAddressInput(it) },
                label = { Text("住所を入力") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 読み込み中インジケータ
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            // 候補住所リスト
            if (suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = { viewModel.selectAddress(suggestion) }
                        )
                    }
                }
            }
            
            // 選択された住所リスト（横スクロール）
            if (selectedAddresses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedAddresses) { address ->
                        val isSelected = address == currentSelectedAddress
                        Timber.tag("AddressWeatherView")
                            .d("住所: ${address.Name}, 選択状態: $isSelected")
                        
                        SelectedAddressItem(
                            address = address,
                            isSelected = isSelected,
                            onClick = { 
                                viewModel.setCurrentAddress(address)
                                Toast.makeText(context, "${address.Name}を選択しました", Toast.LENGTH_SHORT).show()
                            },
                            onRemove = { viewModel.removeAddress(address) }
                        )
                    }
                }
                
                // 天気情報表示（横スクロールページャー）
                Spacer(modifier = Modifier.height(24.dp))
                // 全ての選択済み住所と現在選択中の住所を渡す
                WeatherInfoPager(
                    addresses = selectedAddresses, 
                    currentSelectedAddress = currentSelectedAddress,
                    onAddressSelected = { address -> 
                        // 天気表示のスクロールによって選択された住所を反映
                        Timber.tag("AddressWeatherView")
                            .d("天気表示部分から住所選択: ${address.Name}")
                        viewModel.setCurrentAddress(address)
                    }
                )
            }
        }
    }
} 