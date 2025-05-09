package com.tutorial.kneecast.ui.components.integrated

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.ui.viewmodel.AddressWeatherViewModel
import android.widget.Toast
import com.tutorial.kneecast.data.repository.factory.SavedAddressRepositoryFactory
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherViewModelFactory
import com.tutorial.kneecast.ui.components.addressWeather.SuggestionItem
import com.tutorial.kneecast.ui.components.addressWeather.SelectedAddressItem
import com.tutorial.kneecast.ui.components.addressWeather.WeatherInfoPager
import timber.log.Timber

/**
 * 住所検索と現在地の天気表示を統合したメイン画面
 */
class IntegratedWeatherView {
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        
        // SavedAddressRepositoryを初期化
        val savedAddressRepository = remember { 
            SavedAddressRepositoryFactory.create(context) 
        }
        
        // ViewModelを初期化（ファクトリを使用）
        val viewModel: AddressWeatherViewModel = viewModel(
            factory = AddressWeatherViewModelFactory(savedAddressRepository)
        )
        
        // StateFlowをStateとして収集
        val addressInput by viewModel.addressInput.collectAsState()
        val suggestions by viewModel.addressSuggestions.collectAsState()
        val selectedAddresses by viewModel.selectedAddresses.collectAsState()
        val currentSelectedAddress by viewModel.currentSelectedAddress.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        
        // 現在地選択状態を管理（追加）
        var isCurrentLocationSelected by remember { mutableStateOf(false) }
        
        // エラーメッセージの表示
        LaunchedEffect(error) {
            error?.let { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                // エラーメッセージを表示したらクリア
                viewModel.clearError()
            }
        }
        
        // UI側で選択中の住所を追跡（デバッグ用）
        var selectedAddressName by remember { mutableStateOf("") }
        
        // 現在選択中の住所が変わったらローカル状態も更新
        LaunchedEffect(currentSelectedAddress) {
            currentSelectedAddress?.let {
                selectedAddressName = it.name
                // 住所が選択されたら現在地選択状態をリセット
                isCurrentLocationSelected = false
                Timber.tag("IntegratedWeatherView").d("現在選択中の住所: ${it.name}")
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
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
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
                            onClick = { 
                                viewModel.selectAddress(suggestion)
                                // 住所が選択されたら現在地選択状態をリセット
                                isCurrentLocationSelected = false
                            }
                        )
                    }
                }
            }
            
            // 住所リスト表示エリア（現在地 + 選択された住所）
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 現在地アイテムを最初に表示
                item {
                    CurrentLocationItem(
                        isSelected = isCurrentLocationSelected,
                        onClick = {
                            // 現在地をアクティブにする
                            isCurrentLocationSelected = true
                            // 住所の選択状態をクリア
                            viewModel.clearCurrentAddress()
                            Timber.tag("IntegratedWeatherView").d("現在地が選択されました")
                        }
                    )
                }
                
                // 選択された住所リスト
                if (selectedAddresses.isNotEmpty()) {
                    items(selectedAddresses) { address ->
                        val isSelected = !isCurrentLocationSelected && address == currentSelectedAddress
                        
                        SelectedAddressItem(
                            address = address,
                            isSelected = isSelected,
                            onClick = { 
                                // 住所を選択すると現在地選択状態は解除される
                                isCurrentLocationSelected = false
                                viewModel.setCurrentAddress(address)
                            },
                            onRemove = { viewModel.removeAddress(address) }
                        )
                    }
                }
            }
            
            // 天気情報表示エリア
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isCurrentLocationSelected) {
                // 現在地の天気情報を表示
                CurrentLocationWeatherDisplay()
            } else if (selectedAddresses.isNotEmpty() && currentSelectedAddress != null) {
                // 選択された住所の天気情報を表示
                WeatherInfoPager(
                    addresses = selectedAddresses, 
                    currentSelectedAddress = currentSelectedAddress,
                    onAddressSelected = { address -> 
                        isCurrentLocationSelected = false
                        viewModel.setCurrentAddress(address)
                    }
                )
            } else {
                // 何も選択されていない場合のメッセージ
                Text(
                    text = "現在地または住所を選択して天気を確認してください",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
} 