package com.tutorial.kneecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.tutorial.kneecast.app.AppInitializer
import com.tutorial.kneecast.ui.LocalAppContext
import com.tutorial.kneecast.ui.LocalAppInitializer
import com.tutorial.kneecast.ui.components.MainScreen

/**
 * アプリケーションのメインアクティビティ
 * UI表示とアプリケーションの起動処理のみを担当します
 */
class MainActivity : ComponentActivity() {

    // アプリケーション初期化クラス
    private lateinit var appInitializer: AppInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // アプリケーションの初期化
        initializeApp()
        
        // UIのセットアップ
        setupUI()
    }
    
    /**
     * アプリケーションの初期化
     */
    private fun initializeApp() {
        appInitializer = AppInitializer(this)
        appInitializer.initialize()
    }
    
    /**
     * UIのセットアップ
     */
    private fun setupUI() {
        setContent {
            MaterialTheme {
                // CompositionLocalProviderでContextとAppInitializerを提供
                CompositionLocalProvider(
                    LocalAppContext provides this,
                    LocalAppInitializer provides appInitializer
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MainScreen()
                    }
                }
            }
        }
    }
}