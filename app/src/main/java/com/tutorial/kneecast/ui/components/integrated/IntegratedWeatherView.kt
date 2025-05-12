package com.tutorial.kneecast.ui.components.integrated

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
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
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherCard
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherViewModelFactory
import com.tutorial.kneecast.ui.components.addressWeather.SuggestionItem
import com.tutorial.kneecast.ui.components.addressWeather.SelectedAddressItem
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel
import timber.log.Timber
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

/**
 * 住所検索と現在地の天気表示を統合したメイン画面
 */
class IntegratedWeatherView {
    // ViewModelへの参照を保持
    private var viewModelRef: AddressWeatherViewModel? = null
    
    // 選択待ちの住所を保持するためのキュー
    private val pendingAddresses = mutableListOf<Feature>()
    
    /**
     * 住所検索画面から選択された住所を受け取るメソッド
     */
    fun onAddressReceived(feature: Feature) {
        Timber.d("住所を受け取りました: ${feature.name}")
        val viewModel = viewModelRef
        if (viewModel != null) {
            Timber.d("ViewModelが利用可能なので住所を追加します: ${feature.name}")
            viewModel.selectAddress(feature)
        } else {
            Timber.d("ViewModelがまだないので住所をキューに追加します: ${feature.name}")
            // ViewModelがまだ初期化されていない場合はキューに追加
            pendingAddresses.add(feature)
        }
    }
    
    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        onAddAddressClick: () -> Unit = {} // 住所追加ボタンクリック時のコールバック
    ) {
        val context = LocalContext.current
        
        // SavedAddressRepositoryを初期化
        val savedAddressRepository = remember { 
            SavedAddressRepositoryFactory.create(context) 
        }
        
        // ViewModelを初期化（ファクトリを使用）
        val viewModel: AddressWeatherViewModel = viewModel(
            factory = AddressWeatherViewModelFactory(savedAddressRepository)
        )
        
        // ViewModelの参照を保持
        LaunchedEffect(viewModel) {
            Timber.d("ViewModelの参照を設定")
            viewModelRef = viewModel
            
            // 保留中の住所があれば追加
            if (pendingAddresses.isNotEmpty()) {
                Timber.d("保留中の住所(${pendingAddresses.size}件)を追加")
                pendingAddresses.forEach { feature ->
                    viewModel.selectAddress(feature)
                }
                pendingAddresses.clear()
            }
        }
        
        // ViewModelの参照をDisposableEffectでも管理して確実に保持
        DisposableEffect(viewModel) {
            onDispose {
                // 画面が破棄されるときは参照を保持したまま
                Timber.d("画面が破棄されますが、ViewModelの参照は保持します")
            }
        }
        
        // 位置情報ViewModelを取得
        val locationViewModel = provideLocationViewModel()
        
        // StateFlowをStateとして収集
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
        
        // 表示モード（現在地表示/住所リスト表示）
        var displayMode by remember { mutableStateOf(if (isCurrentLocationSelected) DisplayMode.CURRENT_LOCATION else DisplayMode.SAVED_ADDRESSES) }
        
        // 現在地選択状態が変更されたらdisplayModeも更新
        LaunchedEffect(isCurrentLocationSelected) {
            displayMode = if (isCurrentLocationSelected) DisplayMode.CURRENT_LOCATION else DisplayMode.SAVED_ADDRESSES
        }
        
        Box(modifier = modifier) {
            // 住所検索と天気表示エリアのレイアウト
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 上部コントロールエリア（現在地ボタンと住所追加ボタン）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 現在地ボタン
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .clickable {
                                // 現在地をアクティブにする
                                isCurrentLocationSelected = true
                                displayMode = DisplayMode.CURRENT_LOCATION
                                // 住所の選択状態をクリア
                                viewModel.clearCurrentAddress()
                                // 現在地が選択されたことをログ
                                Timber.tag("IntegratedWeatherView").d("現在地が選択されました")
                                
                                // 位置情報が古いまたは取得できていない場合は再取得
                                if (effectiveLocation == null) {
                                    locationViewModel.fetchLocation()
                                }
                            },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (displayMode == DisplayMode.CURRENT_LOCATION) 4.dp else 2.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (displayMode == DisplayMode.CURRENT_LOCATION) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "現在地",
                                tint = if (displayMode == DisplayMode.CURRENT_LOCATION)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "現在地",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = if (displayMode == DisplayMode.CURRENT_LOCATION)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    // 住所追加ボタン
                    Card(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(vertical = 4.dp)
                            .clickable(onClick = onAddAddressClick),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Box(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "追加",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "住所を追加",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                
                // 保存された住所リスト（常に表示）
                if (selectedAddresses.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 選択された住所リスト
                        items(selectedAddresses) { address ->
                            val isSelected = !isCurrentLocationSelected && address == currentSelectedAddress
                            
                            SelectedAddressItem(
                                address = address,
                                isSelected = isSelected,
                                onClick = { 
                                    // 住所を選択する
                                    isCurrentLocationSelected = false
                                    viewModel.setCurrentAddress(address)
                                },
                                onRemove = { viewModel.removeAddress(address) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 天気情報表示エリア
                Spacer(modifier = Modifier.height(8.dp))
                
                // 天気情報表示
                if (displayMode == DisplayMode.CURRENT_LOCATION && effectiveLocation != null) {
                    // 現在地の天気を表示
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CurrentLocationWeatherCard(
                            location = effectiveLocation
                        )
                    }
                } else if (displayMode == DisplayMode.SAVED_ADDRESSES && selectedAddresses.isNotEmpty()) {
                    // 選択された住所の天気をページャーで表示
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (currentSelectedAddress != null) {
                            // 現在選択中の住所の天気を表示
                            AddressWeatherCard(currentSelectedAddress!!)
                        } else if (selectedAddresses.isNotEmpty()) {
                            // デフォルトで最初の住所を表示
                            AddressWeatherCard(selectedAddresses.first())
                        }
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
    
    // 表示モードを表す列挙型
    enum class DisplayMode {
        CURRENT_LOCATION,  // 現在地表示モード
        SAVED_ADDRESSES    // 保存済み住所表示モード
    }
} 