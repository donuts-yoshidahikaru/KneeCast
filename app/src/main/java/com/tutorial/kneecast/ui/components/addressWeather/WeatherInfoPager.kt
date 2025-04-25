package com.tutorial.kneecast.ui.components.addressWeather

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.ui.viewmodel.AddressPagerViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun WeatherInfoPager(
    addresses: List<Feature>,
    currentSelectedAddress: Feature?,
    onAddressSelected: (Feature) -> Unit
) {
    // ViewModel
    val viewModel: AddressPagerViewModel = viewModel()

    // state 同期
    LaunchedEffect(addresses, currentSelectedAddress) {
        viewModel.syncAddresses(addresses, currentSelectedAddress)
    }
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.addresses.isEmpty()) return

    // PagerState
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedIndex,
        pageCount = { uiState.addresses.size }
    )

    // ViewModel → Pager
    LaunchedEffect(uiState.selectedIndex) {
        if (uiState.selectedIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(uiState.selectedIndex)
        }
    }

    // Pager → ViewModel & 親
    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page in uiState.addresses.indices) {
                    viewModel.onPageChanged(page)           // Toast を ViewModel へ任せる
                    val address = uiState.addresses[page]
                    if (address != currentSelectedAddress) {
                        Timber.tag("WeatherInfoPager").d("ページャーで住所を選択: ${address.Name}")
                        onAddressSelected(address)          // 親には依然通知
                    }
                }
            }
    }

    // UiEvent 受信
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is AddressPagerViewModel.UiEvent.ShowToast -> {
                    // ログにのみ出力し、UIへのToast表示は行わない
                    Timber.d(event.message)
                    // Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // UI 描画
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        key = { page -> "$page-${uiState.addresses[page].let { "${it.Name}_${it.Geometry.Coordinates}" }}" }
    ) { page ->
        AddressWeatherCard(uiState.addresses[page])
    }
}