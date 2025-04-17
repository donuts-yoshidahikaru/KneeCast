package com.tutorial.kneecast.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * アプリケーション全体のパーミッション処理を担当するクラス
 */
class PermissionHandler(private val activity: ComponentActivity) {

    // 位置情報のパーミッション
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // パーミッション結果リスナーインタフェース
    interface PermissionResultListener {
        fun onPermissionGranted()
        fun onPermissionDenied(deniedPermissions: List<String>)
    }

    // パーミッション結果のリスナーマップ
    private val listeners = mutableMapOf<String, PermissionResultListener>()

    // パーミッションランチャー（AppInitializerから提供される）
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    /**
     * パーミッションランチャーを設定
     */
    fun setPermissionLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher = launcher
    }

    /**
     * パーミッション結果を処理
     * ActivityResultLauncherから呼び出される
     */
    fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val key = locationPermissions.joinToString()
        val listener = listeners[key] ?: return
        
        val allGranted = permissions.entries.all { it.value }
        
        if (allGranted) {
            // すべてのパーミッションが許可された
            listener.onPermissionGranted()
        } else {
            // 拒否されたパーミッションがある
            val deniedPermissions = permissions.filter { !it.value }.keys.toList()
            listener.onPermissionDenied(deniedPermissions)
        }
        
        // リスナーを使用後は削除
        listeners.remove(key)
    }

    /**
     * 位置情報パーミッションをリクエスト
     */
    fun requestLocationPermissions(listener: PermissionResultListener) {
        val key = locationPermissions.joinToString()
        listeners[key] = listener
        
        if (hasLocationPermissions()) {
            // すでにパーミッションがある場合は直接コールバック
            listener.onPermissionGranted()
            listeners.remove(key)
        } else {
            // パーミッションリクエスト
            permissionLauncher?.launch(locationPermissions) 
                ?: listener.onPermissionDenied(listOf("パーミッションランチャーが初期化されていません"))
        }
    }

    /**
     * 位置情報のパーミッションがあるかどうかを確認
     */
    fun hasLocationPermissions(): Boolean {
        return locationPermissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
} 