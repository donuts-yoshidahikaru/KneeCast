package com.tutorial.kneecast.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.ui.components.addressWeather.AddressSearchScreen
import com.tutorial.kneecast.ui.components.integrated.IntegratedWeatherView
import com.tutorial.kneecast.ui.components.integrated.onAddressReceived
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * アプリ内の画面遷移を管理するナビゲーションホスト
 */
@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.MAIN_SCREEN
) {
    // 選択された住所を受け取るコールバック関数
    val onAddressSelected: (Feature) -> Unit = remember {
        { feature ->
            Timber.d("住所が選択されました: ${feature.name}")
            onAddressReceived(feature)
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // メインスクリーン
        composable(Destinations.MAIN_SCREEN) {
            // メイン画面の表示
            IntegratedWeatherView(
                modifier = modifier,
                onAddAddressClick = {
                    navController.navigate(Destinations.ADDRESS_SEARCH)
                }
            )
        }
        
        // 住所検索画面
        composable(Destinations.ADDRESS_SEARCH) {
            AddressSearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddressSelected = { feature ->
                    // 住所選択時にコールバックを呼び出す
                    onAddressSelected(feature)
                }
            )
        }
    }
}

/**
 * アプリ内の画面遷移先を定義
 */
object Destinations {
    const val MAIN_SCREEN = "main_screen"
    const val ADDRESS_SEARCH = "address_search"
} 