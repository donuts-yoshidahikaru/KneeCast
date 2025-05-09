package com.tutorial.kneecast.ui.components.integrated

import android.location.Location
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.ui.components.addressWeather.AddressWeatherCard
import com.tutorial.kneecast.ui.viewmodel.AddressPagerViewModel
import com.tutorial.kneecast.ui.viewmodel.provideLocationViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * 現在地と選択された住所の天気情報を表示するページャーコンポーネント
 */
@Composable
fun IntegratedWeatherPager(
    addresses: List<Feature>,
    currentSelectedAddress: Feature?,
    currentLocation: Location?,
    isCurrentLocationSelected: Boolean,
    onAddressSelected: (Feature) -> Unit
) {
    // ViewModel
    val viewModel: AddressPagerViewModel = viewModel()
    val locationViewModel = provideLocationViewModel()
    
    // 現在地のFeatureオブジェクトを作成
    val currentLocationFeature = remember(currentLocation) {
        currentLocation?.let { CurrentLocationFeature.fromLocation(it) }
            ?: CurrentLocationFeature.createDefault()
    }
    
    // すべての表示アイテムリスト（現在地 + 住所リスト）
    val allItems = remember(addresses, currentLocationFeature) {
        listOf(currentLocationFeature) + addresses
    }
    
    // 現在選択されているアイテムのインデックス
    val selectedIndex = remember(isCurrentLocationSelected, currentSelectedAddress, allItems) {
        if (isCurrentLocationSelected) {
            0 // 現在地は常にインデックス0
        } else {
            // 選択されている住所のインデックスを探す（見つからなければ0）
            currentSelectedAddress?.let { selected ->
                allItems.indexOfFirst { it.name == selected.name && it != currentLocationFeature }
                    .takeIf { it >= 0 } ?: 0
            } ?: 0
        }
    }
    
    // PagerState
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { allItems.size }
    )
    
    // 現在のページが変更されたときの処理
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page in allItems.indices) {
                    val selectedFeature = allItems[page]
                    
                    if (page == 0) {
                        // 現在地が選択された
                        Timber.tag("IntegratedWeatherPager").d("現在地が選択されました")
                        onAddressSelected(currentLocationFeature)
                    } else {
                        // 住所が選択された
                        Timber.tag("IntegratedWeatherPager").d("住所が選択されました: ${selectedFeature.name}")
                        onAddressSelected(selectedFeature)
                    }
                }
            }
    }
    
    // 選択されたインデックスが変更されたときにページを更新
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != pagerState.currentPage) {
            Timber.tag("IntegratedWeatherPager").d("選択インデックスが変更されました: $selectedIndex")
            pagerState.animateScrollToPage(selectedIndex)
        }
    }
    
    // ページャー表示
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        key = { page -> "$page-${allItems[page].let { "${it.name}_${it.geometry.coordinates}" }}" }
    ) { page ->
        val item = allItems[page]
        
        if (page == 0) {
            // 現在地の天気表示
            if (currentLocation != null) {
                val coordinates = Coordinates(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude
                )
                CurrentLocationWeatherCard(coordinates)
            } else {
                // 位置情報が取得できていない場合
                LocationLoadingCard {
                    locationViewModel.fetchLocation()
                }
            }
        } else {
            // 通常の住所の天気表示
            AddressWeatherCard(item)
        }
    }
} 