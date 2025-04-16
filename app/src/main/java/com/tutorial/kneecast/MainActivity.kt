package com.tutorial.kneecast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.tutorial.kneecast.ui.components.MainScreen
import com.tutorial.kneecast.location.GPSLocationManager
import com.tutorial.kneecast.ui.LocalAppContext

class MainActivity : ComponentActivity() {

    private var locationCallback: GPSLocationManager.MyLocationCallback? = null
    private var initialLocationFetched = false

    // 位置情報のパーミッション結果を処理するランチャー
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // 位置情報の権限が許可された場合
            locationCallback?.let {
                GPSLocationManager(this).requestLocationUpdates(it)
            }
        } else {
            // 権限が拒否された場合
            locationCallback?.onLocationError("位置情報の許可が必要です")
        }
    }

    fun setLocationCallback(callback: GPSLocationManager.MyLocationCallback) {
        this.locationCallback = callback
        
        // アプリ起動時に一度だけ位置情報を取得（パーミッションがあれば）
        if (!initialLocationFetched && 
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initialLocationFetched = true
            GPSLocationManager(this).requestLocationUpdates(callback)
        }
    }

    fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // CompositionLocalProviderでContextを提供
                CompositionLocalProvider(LocalAppContext provides this) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // Contextを渡す必要がなくなった
                        MainScreen().Content()
                    }
                }
            }
        }
    }
}