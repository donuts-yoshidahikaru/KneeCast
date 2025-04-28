package com.tutorial.kneecast.data

import android.content.Context
import com.tutorial.kneecast.MainActivity
import com.tutorial.kneecast.app.AppInitializer
import com.tutorial.kneecast.location.GPSLocationManager
import com.tutorial.kneecast.permissions.PermissionHandler

/**
 * 位置情報関連の処理を一元管理するリポジトリクラス
 */
class LocationRepository(private val context: Context, private val appInitializer: AppInitializer? = null) {
    
    private var locationCallback: GPSLocationManager.MyLocationCallback? = null
    private var permissionHandler: PermissionHandler? = null
    
    init {
        if (context is MainActivity) {
            permissionHandler = PermissionHandler(context)
        }
    }
    
    /**
     * 位置情報取得のコールバックを設定し、パーミッションがある場合は位置情報を取得
     */
    fun setLocationCallback(callback: GPSLocationManager.MyLocationCallback) {
        this.locationCallback = callback

        if (permissionHandler?.hasLocationPermissions() == true) {
            requestLocationUpdates(callback)
        } else {
            requestLocationPermissions()
        }
    }
    
    /**
     * 位置情報のパーミッションを要求
     */
    fun requestLocationPermissions() {
        if (appInitializer != null) {
            appInitializer.getPermissionHandler().requestLocationPermissions(object : PermissionHandler.PermissionResultListener {
                override fun onPermissionGranted() {
                    locationCallback?.let { callback ->
                        requestLocationUpdates(callback)
                    }
                }
                
                override fun onPermissionDenied() {
                    locationCallback?.onLocationError("位置情報の許可が必要です。設定から許可してください。")
                }
            })
        } else {
            permissionHandler?.requestLocationPermissions(object : PermissionHandler.PermissionResultListener {
                override fun onPermissionGranted() {
                    locationCallback?.let { callback ->
                        requestLocationUpdates(callback)
                    }
                }
                
                override fun onPermissionDenied() {
                    locationCallback?.onLocationError("位置情報の許可が必要です。設定から許可してください。")
                }
            })
        }
    }

    /**
     * 位置情報の更新をリクエスト
     */
    private fun requestLocationUpdates(callback: GPSLocationManager.MyLocationCallback) {
        try {
            GPSLocationManager(context).requestLocationUpdates(callback)
        } catch (e: Exception) {
            callback.onLocationError("位置情報の取得に失敗しました: ${e.localizedMessage}")
        }
    }
} 