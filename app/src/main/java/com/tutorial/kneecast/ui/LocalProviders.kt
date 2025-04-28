package com.tutorial.kneecast.ui

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.tutorial.kneecast.app.AppInitializer

// アプリケーション全体で使用できるContextを提供するCompositionLocal
val LocalAppContext = compositionLocalOf<Context> { 
    error("CompositionLocalにContextが提供されていません") 
}

// アプリケーション全体で使用できるAppInitializerを提供するCompositionLocal
val LocalAppInitializer = compositionLocalOf<AppInitializer> {
    error("CompositionLocalにAppInitializerが提供されていません")
} 