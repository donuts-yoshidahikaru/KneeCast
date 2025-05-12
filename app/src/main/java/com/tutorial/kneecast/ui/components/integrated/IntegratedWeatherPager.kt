package com.tutorial.kneecast.ui.components.integrated

import android.location.Location
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    
    // 現在地のFeatureオブジェクトを作成
    val currentLocationFeature = remember(currentLocation) {
        currentLocation?.let { CurrentLocationFeature.fromLocation(it) }
            ?: CurrentLocationFeature.createDefault()
    }
    
    // すべての表示アイテムリスト（現在地 + 住所リスト）
    val allItems = remember(addresses, currentLocationFeature) {
        listOf(currentLocationFeature) + addresses
    }
    
    // 選択されたインデックスを計算
    val selectedIndex = if (isCurrentLocationSelected) {
        0 // 現在地は常にインデックス0
    } else {
        // 選択されている住所のインデックスを探す
        currentSelectedAddress?.let { selected ->
            // addressesリスト内での位置を探し、見つかったら+1する（現在地があるため）
            addresses.indexOf(selected).let { index ->
                if (index >= 0) index + 1 else 0
            }
        } ?: 0
    }
    
    // PagerStateを作成（pageCountは固定値ではなく関数を使用）
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { allItems.size }
    )
    
    // デバッグログ
    LaunchedEffect(addresses, currentSelectedAddress, isCurrentLocationSelected) {
        Timber.tag("IntegratedWeatherPager").d(
            "状態更新: 住所数=${addresses.size}, " +
            "選択住所=${currentSelectedAddress?.name ?: "なし"}, " +
            "現在地選択=$isCurrentLocationSelected, " +
            "計算インデックス=$selectedIndex"
        )
    }
    
    // 選択インデックスが変更されたときにページを更新
    // LaunchedEffectではなくSideEffectを使い、再コンポジション中にも適切に処理
    SideEffect {
        if (selectedIndex != pagerState.currentPage && allItems.isNotEmpty()) {
            Timber.tag("IntegratedWeatherPager").d(
                "ページ遷移: ${pagerState.currentPage} → $selectedIndex"
            )
            coroutineScope.launch {
                // アニメーションの感覚をなめらかにするためにanimateScrollToPageを使用
                pagerState.animateScrollToPage(
                    page = selectedIndex,
                    // アニメーション設定を追加して遷移をスムーズに
                    pageOffsetFraction = 0f
                )
            }
        }
    }
    
    // 現在のページが変更されたときの処理
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                // インデックスが範囲内かチェック
                if (page in allItems.indices) {
                    val selectedFeature = allItems[page]
                    
                    // 既に選択中のアイテムと同じなら処理しない
                    val isAlreadySelected = (page == 0 && isCurrentLocationSelected) ||
                            (page > 0 && currentSelectedAddress == selectedFeature)
                    
                    if (!isAlreadySelected) {
                        if (page == 0) {
                            // 現在地が選択された
                            Timber.tag("IntegratedWeatherPager").d("現在地が選択されました（ページャー）")
                            onAddressSelected(currentLocationFeature)
                        } else {
                            // 住所が選択された
                            Timber.tag("IntegratedWeatherPager").d("住所が選択されました（ページャー）: ${selectedFeature.name}")
                            onAddressSelected(selectedFeature)
                        }
                    }
                }
            }
    }
    
    // ページャー表示
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        // キーを単純化してパフォーマンスを向上
        key = { page -> "page_$page" }
    ) { page ->
        // インデックスがリストの範囲内かチェック
        if (page !in allItems.indices) return@HorizontalPager
        
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