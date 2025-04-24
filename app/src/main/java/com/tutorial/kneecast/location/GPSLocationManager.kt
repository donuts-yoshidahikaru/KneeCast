package com.tutorial.kneecast.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

class GPSLocationManager(context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(10000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMinUpdateIntervalMillis(0)
        .setMaxUpdates(1)  // 1回だけ更新を受け取る
        .build()
    
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
            .addOnFailureListener { e -> 
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

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                if (!locationAvailability.isLocationAvailable) {
                    removeLocationUpdates()
                    callback.onLocationError("位置情報サービスが利用できません")
                }
            }
        }
        
        try {
            // 位置情報の更新をリクエスト（一度だけ）
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback as LocationCallback,
                null
            ).addOnFailureListener { e ->
                removeLocationUpdates()
                callback.onLocationError("位置情報の取得に失敗しました: ${e.localizedMessage}")
            }
        } catch (e: Exception) {
            removeLocationUpdates()
            callback.onLocationError("位置情報の取得に失敗しました: ${e.localizedMessage}")
        }
    }
    
    private fun removeLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }
}

