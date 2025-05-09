package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tutorial.kneecast.ui.components.integrated.IntegratedWeatherView

/**
 * アプリのメイン画面
 * 住所検索と現在地の両方からの天気情報を単一のビューで表示する
 */
class MainScreen {
    @Composable
    fun Content() {
        // 統合されたビューを表示
        val integratedView = IntegratedWeatherView()
        
        integratedView.Content(
            modifier = Modifier.fillMaxSize()
        )
    }
}
