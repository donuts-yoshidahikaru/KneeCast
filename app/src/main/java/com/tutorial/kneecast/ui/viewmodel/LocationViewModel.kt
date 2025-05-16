package com.tutorial.kneecast.ui.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tutorial.kneecast.app.AppInitializer
import com.tutorial.kneecast.location.GPSLocationManager

/**
 * 位置情報の状態管理を行うViewModel
 */
class LocationViewModel : ViewModel() {
    
    // 位置情報取得中の状態を表すLiveData
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    
    // 取得した位置情報を保持するLiveData
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> get() = _location
    
    // エラーメッセージを保持するLiveData
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    
    // 初期化済みフラグ
    private var initialized = false
    
    // AppInitializerのインスタンス
    private lateinit var appInitializer: AppInitializer
    
    /**
     * ViewModelの初期化
     * @param appInitializer アプリケーション初期化クラス
     */
    fun initialize(appInitializer: AppInitializer) {
        if (initialized) return
        
        this.appInitializer = appInitializer
        
        // 位置情報のコールバックを設定
        appInitializer.setLocationCallback(object : GPSLocationManager.MyLocationCallback {
            override fun onLocationResult(location: Location?) {
                _loading.postValue(false)
                if (location != null) {
                    _location.postValue(location)
                    _error.postValue(null)
                } else {
                    _error.postValue("位置情報を取得できませんでした")
                }
            }
            
            override fun onLocationError(error: String) {
                _loading.postValue(false)
                _error.postValue(error)
                _location.postValue(null)
            }
        })
        
        initialized = true
    }
    
    /**
     * 位置情報を取得
     */
    fun fetchLocation() {
        if (!initialized) {
            _error.value = "ViewModelが初期化されていません"
            return
        }
        
        _loading.value = true
        _error.value = null
        appInitializer.requestLocationPermissions()
    }

} 