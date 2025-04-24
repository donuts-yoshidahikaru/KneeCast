package com.tutorial.kneecast.app

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.tutorial.kneecast.data.LocationRepository
import com.tutorial.kneecast.location.GPSLocationManager
import com.tutorial.kneecast.permissions.PermissionHandler

/**
 * アプリケーションの初期化処理を担当するクラス
 * MainActivityから処理を分離し、機能単位での初期化を担当します
 */
class AppInitializer(private val activity: ComponentActivity) {
    
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var locationRepository: LocationRepository
    
    // 位置情報のパーミッション結果を処理するランチャー
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    
    /**
     * アプリケーションの初期化を行います
     */
    fun initialize() {
        // パーミッションランチャーを初期化
        initializePermissionLauncher()
        
        // パーミッションハンドラーを初期化
        initializePermissionHandler()
        
        // 位置情報リポジトリを初期化
        initializeLocationRepository()
    }
    
    /**
     * パーミッションランチャーを初期化
     */
    private fun initializePermissionLauncher() {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (::permissionHandler.isInitialized) {
                permissionHandler.handlePermissionResult(permissions)
            }
        }
    }
    
    /**
     * パーミッションハンドラーを初期化
     */
    private fun initializePermissionHandler() {
        permissionHandler = PermissionHandler(activity)
        
        // ランチャーがすでに初期化されている場合は設定
        if (::permissionLauncher.isInitialized) {
            permissionHandler.setPermissionLauncher(permissionLauncher)
        }
    }
    
    /**
     * 位置情報リポジトリを初期化
     */
    private fun initializeLocationRepository() {
        // thisを渡してAppInitializerのインスタンスを提供
        locationRepository = LocationRepository(activity, this)
    }
    
    /**
     * パーミッションハンドラーを取得
     * LocationRepositoryがパーミッションリクエストに使用
     */
    fun getPermissionHandler(): PermissionHandler {
        return permissionHandler
    }
    
    /**
     * 位置情報コールバックを設定
     * 互換性のために残し、内部でリポジトリに委譲
     */
    fun setLocationCallback(callback: GPSLocationManager.MyLocationCallback) {
        locationRepository.setLocationCallback(callback)
    }
    
    /**
     * 位置情報のパーミッションをリクエスト
     * 互換性のために残し、内部でリポジトリに委譲
     */
    fun requestLocationPermissions() {
        locationRepository.requestLocationPermissions()
    }
} 