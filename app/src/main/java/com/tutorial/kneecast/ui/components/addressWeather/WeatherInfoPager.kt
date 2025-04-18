package com.tutorial.kneecast.ui.components.addressWeather

import android.util.Log
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

@Composable
fun WeatherInfoPager(
    addresses: List<Feature>,
    currentSelectedAddress: Feature?,
    onAddressSelected: (Feature) -> Unit
) {
    val viewModel: AddressPagerViewModel = viewModel()

    LaunchedEffect(addresses, currentSelectedAddress) {
        viewModel.syncAddresses(addresses, currentSelectedAddress)
    }

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.addresses.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = uiState.selectedIndex,
        pageCount = { uiState.addresses.size }
    )

    LaunchedEffect(uiState.selectedIndex) {
        if (uiState.selectedIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(uiState.selectedIndex)
        }
    }

    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page in uiState.addresses.indices) {
                    viewModel.onPageChanged(page)

                    val address = uiState.addresses[page]
                    if (address != currentSelectedAddress) {
                        Toast.makeText(
                            context,
                            "選択住所を ${address.Name} に変更",
                            Toast.LENGTH_SHORT
                        ).show()
                        onAddressSelected(address)
                    }
                }
            }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        key = { page -> uiState.addresses[page].let { "${it.Name}_${it.Geometry.Coordinates}" } }
    ) { page ->
        AddressWeatherCard(uiState.addresses[page])
    }
}