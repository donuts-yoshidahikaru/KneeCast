package com.tutorial.kneecast.ui

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

// アプリケーション全体で使用できるContextを提供するCompositionLocal
val LocalAppContext = compositionLocalOf<Context> { 
    error("CompositionLocalにContextが提供されていません") 
} 