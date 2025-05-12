package com.tutorial.kneecast.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tutorial.kneecast.ui.navigation.MainNavHost

/**
 * アプリのメイン画面
 * ナビゲーションホストを使用して画面遷移を管理
 */
class MainScreen {
    @Composable
    fun Content() {
        // ナビゲーションホストを表示
        MainNavHost(
            modifier = Modifier.fillMaxSize()
        )
    }
}
