package com.tutorial.kneecast.ui.components.addressWeather

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.data.model.Coordinates
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.ui.components.WeatherScreen
import com.tutorial.kneecast.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast

/**
 * 選択された住所の天気情報を表示するコンポーネント
 * ここでは単一の住所の表示ではなく、全ての選択済み住所を横スクロールで表示
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherInfoContent(
    addresses: List<Feature>, 
    currentSelectedAddress: Feature?,
    onAddressSelected: (Feature) -> Unit
) {
    if (addresses.isEmpty()) return
    
    val context = LocalContext.current
    
    // 表示対象のアドレスを決定（現在選択中または先頭）
    val currentAddress = currentSelectedAddress ?: addresses.first()
    // 現在選択中のアドレスのインデックスを取得
    val initialPage = addresses.indexOf(currentAddress).coerceAtLeast(0)
    
    Log.d("WeatherInfoContent", "Current address: ${currentAddress.Name}, initialPage: $initialPage")
    
    // 現在表示しているページの住所（ローカル状態）
    val currentPageAddress = remember { mutableStateOf(currentAddress) }
    
    // PagerStateを作成
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { addresses.size }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    // 現在選択中のアドレスが変更されたらページを切り替え
    LaunchedEffect(currentSelectedAddress) {
        if (currentSelectedAddress == null) return@LaunchedEffect
        
        val index = addresses.indexOf(currentSelectedAddress)
        Log.d("WeatherInfoContent", "Address changed. New index: $index, current page: ${pagerState.currentPage}")
        if (index >= 0 && index != pagerState.currentPage) {
            try {
                pagerState.animateScrollToPage(index)
                // 明示的なフィードバック
                Toast.makeText(context, "表示住所を ${currentSelectedAddress.Name} に変更", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("WeatherInfoContent", "Error scrolling to page: $index", e)
            }
        }
    }
    
    // ページが変更されたら選択中の住所も更新する
    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            Log.d("WeatherInfoContent", "Page changed to: $page")
            if (page >= 0 && page < addresses.size) {
                val address = addresses[page]
                currentPageAddress.value = address
                
                Log.d("WeatherInfoContent", "Selected address from page: ${address.Name}")
                // 選択中の住所と異なる場合のみ通知
                if (address != currentSelectedAddress) {
                    Log.d("WeatherInfoContent", "Notifying address selection: ${address.Name}")
                    // 明示的なフィードバック
                    Toast.makeText(context, "選択住所を ${address.Name} に変更", Toast.LENGTH_SHORT).show()
                    onAddressSelected(address)
                }
            }
        }
    }
    
    // 横スクロールのPagerを使用
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val address = addresses[page]
        Log.d("WeatherInfoContent", "Rendering page $page with address: ${address.Name}")
        // 各ページで住所ごとの天気情報を表示
        AddressWeatherPage(address)
    }
}

/**
 * 各住所ごとの天気情報ページ
 */
@Composable
private fun AddressWeatherPage(address: Feature) {
    val coordinates = address.Geometry.Coordinates.split(",")
    // 緯度・経度を取得できる場合
    if (coordinates.size >= 2) {
        val longitude = coordinates[0].toDoubleOrNull()
        val latitude = coordinates[1].toDoubleOrNull()
        
        if (longitude != null && latitude != null) {
            // 天気情報を表示するコンポーネント
            WeatherDisplay(address.Name, longitude, latitude)
        }
    }
}

/**
 * 天気情報表示用のコンポーネント
 * 独立したWeatherViewModelインスタンスを使用
 */
@Composable
fun WeatherDisplay(addressName: String, longitude: Double, latitude: Double) {
    // ViewModelStoreOwnerを取得
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    
    // 独立したWeatherViewModelインスタンスを作成するためにFactoryを使用
    val factory = remember { AddressWeatherViewModelFactory() }
    val weatherViewModel: WeatherViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = factory,
        key = "address_weather_${addressName}_${longitude}_${latitude}" // 一意のキーを使用
    )
    
    // 座標オブジェクトを作成
    val coordinates = remember(longitude, latitude) {
        Coordinates(latitude, longitude)
    }
    
    // コンポーネントが表示されたときに天気情報を取得
    LaunchedEffect(coordinates) {
        weatherViewModel.fetchWeatherInfo(addressName, coordinates)
    }
    
    // 既存のWeatherScreenを使用して天気情報を表示
    Box(modifier = Modifier.fillMaxWidth()) {
        WeatherScreen(weatherViewModel = weatherViewModel)
    }
} 