package com.tutorial.kneecast.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

@Suppress("DEPRECATION")
class GPSLocationManager(context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        numUpdates = 1  // 一回のみ更新を受け取る
        interval = 0    // できるだけ早く
    }
    
    private var locationCallback: LocationCallback? = null

    interface MyLocationCallback {
        fun onLocationResult(location: Location?)
        fun onLocationError(error: String)
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(callback: MyLocationCallback) {
        // まず既存のコールバックがあれば削除
        removeLocationUpdates()
        
        // lastLocationを試してみる
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // キャッシュされた位置情報があればそれを使用
                    callback.onLocationResult(location)
                } else {
                    // なければ新しく取得
                    requestSingleUpdate(callback)
                }
            }
            .addOnFailureListener { 
                // エラーの場合も新しく取得
                requestSingleUpdate(callback)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestSingleUpdate(callback: MyLocationCallback) {
        // 新しいLocationCallbackを作成
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation
                
                // 必ず更新を停止
                removeLocationUpdates()
                
                if (lastLocation != null) {
                    callback.onLocationResult(lastLocation)
                } else {
                    callback.onLocationError("位置情報を取得できませんでした")
                }
            }
        }
        
        // 位置情報の更新をリクエスト（一度だけ）
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback as LocationCallback,
            null
        )
    }
    
    private fun removeLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }
}

