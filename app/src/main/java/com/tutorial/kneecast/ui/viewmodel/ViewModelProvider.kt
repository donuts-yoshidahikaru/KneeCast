package com.tutorial.kneecast.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.ui.LocalAppInitializer

/**
 * Compose関数内でLocationViewModelを取得するためのヘルパー関数
 * 自動的に初期化も行います
 */
@Composable
fun provideLocationViewModel(): LocationViewModel {
    val appInitializer = LocalAppInitializer.current
    val viewModel: LocationViewModel = viewModel()
    
    // ViewModelの初期化（既に初期化済みの場合は内部でスキップされる）
    remember(viewModel,appInitializer) {
        viewModel.initialize(appInitializer)
        viewModel
    }
    
    return viewModel
}

/**
 * Compose関数内でWeatherViewModelを取得するためのヘルパー関数
 */
@Composable
fun provideWeatherViewModel(): WeatherViewModel {
    return viewModel()
} 