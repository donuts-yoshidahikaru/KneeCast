package com.tutorial.kneecast.ui.components.addressWeather

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tutorial.kneecast.data.model.Feature
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherInfoPager(
    addresses: List<Feature>,
    currentSelectedAddress: Feature?,
    onAddressSelected: (Feature) -> Unit
) {
    if (addresses.isEmpty()) return

    val context = LocalContext.current

    val currentAddress = currentSelectedAddress ?: addresses.first()
    val initialPage = addresses.indexOf(currentAddress).coerceAtLeast(0)

    Log.d("WeatherInfoPager", "Current address: ${currentAddress.Name}, initialPage: $initialPage")

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { addresses.size }
    )

    LaunchedEffect(currentSelectedAddress) {
        currentSelectedAddress ?: return@LaunchedEffect
        val index = addresses.indexOf(currentSelectedAddress)
        if (index >= 0 && index != pagerState.currentPage) {
            try {
                pagerState.animateScrollToPage(index)
                Toast.makeText(context, "表示住所を ${currentSelectedAddress.Name} に変更", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("WeatherInfoPager", "Error scrolling to page: $index", e)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page in addresses.indices) {
                    val address = addresses[page]
                    if (address != currentSelectedAddress) {
                        Toast.makeText(context, "選択住所を ${address.Name} に変更", Toast.LENGTH_SHORT).show()
                        onAddressSelected(address)
                    }
                }
            }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val address = addresses[page]
        AddressWeatherCard(address)
    }
}