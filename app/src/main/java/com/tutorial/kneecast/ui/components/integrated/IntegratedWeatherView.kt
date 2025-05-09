package com.tutorial.kneecast.ui.components.integrated

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.ui.viewmodel.AddressWeatherViewModel
import android.location.Location
import android.widget.Toast
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.repository.factory.SavedAddressRepositoryFactory
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherViewModelFactory
import com.tutorial.kneecast.ui.components.addressWeather.SuggestionItem
import com.tutorial.kneecast.ui.components.addressWeather.SelectedAddressItem
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel
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
        
        // 位置情報ViewModelを取得
        val locationViewModel = provideLocationViewModel()
        
        // StateFlowをStateとして収集
        val addressInput by viewModel.addressInput.collectAsState()
        val suggestions by viewModel.addressSuggestions.collectAsState()
        val selectedAddresses by viewModel.selectedAddresses.collectAsState()
        val currentSelectedAddress by viewModel.currentSelectedAddress.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        
        // 位置情報をStateとして観測
        val location by locationViewModel.location.observeAsState()
        val locationLoading by locationViewModel.loading.observeAsState(false)
        val locationError by locationViewModel.error.observeAsState()
        
        // 現在地選択状態を管理（永続化された値を初期値として使用）
        var isCurrentLocationSelected by remember { 
            mutableStateOf(savedAddressRepository.isCurrentLocationSelected()) 
        }
        
        // 最後に保存された位置情報を取得
        val lastKnownLocation = remember {
            savedAddressRepository.getLastKnownLocation()?.let { (lat, lon) ->
                Location("cached").apply {
                    latitude = lat
                    longitude = lon
                }
            }
        }
        
        // 現在の位置情報（リアルタイムまたはキャッシュ）
        val effectiveLocation = remember(location, lastKnownLocation) {
            location ?: lastKnownLocation
        }
        
        // 画面表示時に一度だけ位置情報を自動的に取得
        LaunchedEffect(Unit) {
            locationViewModel.fetchLocation()
        }
        
        // 位置情報が更新されたら保存
        LaunchedEffect(location) {
            location?.let {
                savedAddressRepository.saveLastKnownLocation(it.latitude, it.longitude)
                Timber.d("位置情報を保存しました: ${it.latitude}, ${it.longitude}")
            }
        }
        
        // 現在地選択状態が変更されたら永続化
        LaunchedEffect(isCurrentLocationSelected) {
            savedAddressRepository.setCurrentLocationSelected(isCurrentLocationSelected)
        }
        
        // エラーメッセージの表示（住所関連）
        LaunchedEffect(error) {
            error?.let { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
        
        // エラーメッセージの表示（位置情報関連）
        LaunchedEffect(locationError) {
            locationError?.let { errorMessage ->
                Toast.makeText(context, "位置情報エラー: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 現在選択中の住所が変わったらローカル状態も更新
        LaunchedEffect(currentSelectedAddress) {
            currentSelectedAddress?.let {
                // 住所が選択されたら現在地選択状態をリセット
                if (!isCurrentLocationSelected) {
                    Timber.tag("IntegratedWeatherView").d("現在選択中の住所: ${it.name}")
                }
            }
        }
        
        Box(modifier = modifier) {
            // 住所検索と天気表示エリアのレイアウト
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
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
                                // 現在地が選択されたことをログ
                                Timber.tag("IntegratedWeatherView").d("現在地が選択されました")
                                
                                // 位置情報が古いまたは取得できていない場合は再取得
                                if (effectiveLocation == null) {
                                    locationViewModel.fetchLocation()
                                }
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
                
                // 統合されたページャーで天気情報を表示
                if (selectedAddresses.isNotEmpty() || isCurrentLocationSelected) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IntegratedWeatherPager(
                            addresses = selectedAddresses,
                            currentSelectedAddress = currentSelectedAddress,
                            currentLocation = effectiveLocation,
                            isCurrentLocationSelected = isCurrentLocationSelected,
                            onAddressSelected = { feature ->
                                if (CurrentLocationFeature.isCurrentLocation(feature)) {
                                    // 現在地が選択された
                                    isCurrentLocationSelected = true
                                    viewModel.clearCurrentAddress()
                                } else {
                                    // 住所が選択された
                                    isCurrentLocationSelected = false
                                    // 選択された住所をリストから探して設定
                                    selectedAddresses.find { it.name == feature.name }?.let {
                                        viewModel.setCurrentAddress(it)
                                    }
                                }
                            }
                        )
                    }
                } else {
                    // 何も選択されていない場合のメッセージ
                    Text(
                        text = "現在地または住所を選択して天気を確認してください",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // 読み込み中インジケータ（最上部に棒状で表示）
            if (isLoading || locationLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f)  // 最前面に表示
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
} 