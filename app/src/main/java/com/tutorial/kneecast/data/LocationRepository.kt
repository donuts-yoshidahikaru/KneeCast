package com.tutorial.kneecast.data

import android.content.Context
import com.tutorial.kneecast.MainActivity
import com.tutorial.kneecast.location.GPSLocationManager
import com.tutorial.kneecast.permissions.PermissionHandler

/**
 * 位置情報関連の処理を一元管理するリポジトリクラス
 */
class LocationRepository(private val context: Context) {
    
    private var locationCallback: GPSLocationManager.MyLocationCallback? = null
    private var initialLocationFetched = false
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
        
        // アプリ起動時に一度だけ位置情報を取得（パーミッションがあれば）
        if (!initialLocationFetched && 
            permissionHandler?.hasLocationPermissions() == true) {
            initialLocationFetched = true
            requestLocationUpdates(callback)
        }
    }
    
    /**
     * 位置情報のパーミッションを要求
     */
    fun requestLocationPermissions() {
        permissionHandler?.requestLocationPermissions(object : PermissionHandler.PermissionResultListener {
            override fun onPermissionGranted() {
                locationCallback?.let { callback ->
                    requestLocationUpdates(callback)
                }
            }
            
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                locationCallback?.onLocationError("位置情報の許可が必要です")
            }
        })
    }
    
    /**
     * 位置情報を取得
     */
    
    /**
     * 位置情報の更新をリクエスト
     */
    private fun requestLocationUpdates(callback: GPSLocationManager.MyLocationCallback) {
        GPSLocationManager(context).requestLocationUpdates(callback)
    }
} 