package com.tutorial.kneecast.data.repository

import com.tutorial.kneecast.data.local.dao.SavedAddressDao
import com.tutorial.kneecast.data.mapper.AddressMapper
import com.tutorial.kneecast.data.model.Feature
import timber.log.Timber
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 保存された住所と現在地の選択状態を管理するリポジトリ
 */
class SavedAddressRepository(
    private val savedAddressDao: SavedAddressDao,
    context: Context? = null
) {
    // 選択状態を保存するSharedPreferences
    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // コルーチンスコープ
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val PREFS_NAME = "kneecast_location_prefs"
        private const val KEY_CURRENT_LOCATION_SELECTED = "is_current_location_selected"
        private const val KEY_LAST_KNOWN_LAT = "last_known_latitude"
        private const val KEY_LAST_KNOWN_LON = "last_known_longitude"
    }
    
    /**
     * 現在地が選択されているかどうかを取得
     */
    fun isCurrentLocationSelected(): Boolean {
        return prefs?.getBoolean(KEY_CURRENT_LOCATION_SELECTED, false) == true
    }
    
    /**
     * 現在地の選択状態を設定
     */
    fun setCurrentLocationSelected(selected: Boolean) {
        prefs?.edit { putBoolean(KEY_CURRENT_LOCATION_SELECTED, selected) }
        
        if (selected) {
            // 現在地を選択した場合は、住所の選択状態をクリア（コルーチン内で実行）
            coroutineScope.launch {
                clearSelectedAddressInDb()
            }
        }
        
        Timber.d("現在地の選択状態を変更: $selected")
    }
    
    /**
     * 最後に取得した位置情報を保存
     */
    fun saveLastKnownLocation(latitude: Double, longitude: Double) {
        prefs?.edit {
            putFloat(KEY_LAST_KNOWN_LAT, latitude.toFloat())
            putFloat(KEY_LAST_KNOWN_LON, longitude.toFloat())
        }
        
        Timber.d("最後に取得した位置情報を保存: $latitude, $longitude")
    }
    
    /**
     * 最後に取得した位置情報を取得
     */
    fun getLastKnownLocation(): Pair<Double, Double>? {
        val lat = prefs?.getFloat(KEY_LAST_KNOWN_LAT, Float.NaN) ?: Float.NaN
        val lon = prefs?.getFloat(KEY_LAST_KNOWN_LON, Float.NaN) ?: Float.NaN
        
        return if (!lat.isNaN() && !lon.isNaN()) {
            Pair(lat.toDouble(), lon.toDouble())
        } else {
            null
        }
    }
    
    /**
     * DB上で選択された住所の選択状態をクリア（内部メソッド）
     */
    private suspend fun clearSelectedAddressInDb() {
        try {
            val selectedAddress = savedAddressDao.getSelectedAddress()
            
            if (selectedAddress != null) {
                // 選択状態を解除して更新
                val updatedAddress = selectedAddress.copy(isSelected = false)
                savedAddressDao.insertAddress(updatedAddress)
                Timber.d("DB上の選択住所をクリアしました: ${selectedAddress.addressName}")
            }
        } catch (e: Exception) {
            Timber.e(e, "DB上の選択住所クリアに失敗しました")
        }
    }
    
    suspend fun getAllAddresses(): List<Feature> {
        return AddressMapper.toFeatures(savedAddressDao.getAllAddresses())
    }

    suspend fun getSelectedAddress(): Feature? {
        return savedAddressDao.getSelectedAddress()?.let {
            AddressMapper.toFeature(it)
        }
    }

    suspend fun saveAddress(feature: Feature, isSelected: Boolean = false): Long {
        // 住所名と座標から既存の住所を検索
        val (longitude, latitude) = feature.geometry.coordinates
            .split(",")
            .map { it.trim().toDouble() }
            
        val existingAddress = savedAddressDao.findAddressByNameAndCoordinates(
            feature.name, latitude, longitude
        )
        
        // 住所が選択された場合、現在地選択状態をクリア
        if (isSelected) {
            setCurrentLocationSelected(false)
        }
        
        // 既存の住所が見つかった場合は、それを更新
        if (existingAddress != null) {
            Timber.d("既存の住所を更新: ${feature.name}")
            val updatedAddress = existingAddress.copy(isSelected = isSelected)
            val id = savedAddressDao.insertAddress(updatedAddress)
            
            if (isSelected) {
                savedAddressDao.updateSelectedAddress(id)
            }
            
            return id
        }
        
        // 新規住所の保存
        Timber.d("新規住所を保存: ${feature.name}")
        val savedAddress = AddressMapper.fromFeature(feature, isSelected)
        val id = savedAddressDao.insertAddress(savedAddress)
        
        if (isSelected) {
            savedAddressDao.updateSelectedAddress(id)
        }
        
        return id
    }

    suspend fun deleteAddress(feature: Feature) {
        try {
            // 住所名で削除
            Timber.d("住所を削除: ${feature.name}")
            savedAddressDao.deleteAddressByName(feature.name)
        } catch (e: Exception) {
            Timber.e(e, "住所の削除に失敗: ${feature.name}")
            throw e
        }
    }

    suspend fun updateSelectedAddress(feature: Feature) {
        try {
            // 住所が選択された場合、現在地選択状態をクリア
            setCurrentLocationSelected(false)
            
            val savedAddress = AddressMapper.fromFeature(feature, true)
            
            // 既存の住所を検索
            val existingAddress = savedAddressDao.findAddressByName(feature.name)
            
            val id = if (existingAddress != null) {
                // 既存の住所を更新
                val updatedAddress = existingAddress.copy(isSelected = true)
                savedAddressDao.insertAddress(updatedAddress)
            } else {
                // 新規住所として保存
                savedAddressDao.insertAddress(savedAddress)
            }
            
            savedAddressDao.updateSelectedAddress(id)
        } catch (e: Exception) {
            Timber.e(e, "選択住所の更新に失敗: ${feature.name}")
            throw e
        }
    }

    suspend fun clearSelectedAddress() {
        try {
            // 選択中の住所を取得
            val selectedAddress = savedAddressDao.getSelectedAddress()
            
            if (selectedAddress != null) {
                // 選択状態を解除して更新
                val updatedAddress = selectedAddress.copy(isSelected = false)
                savedAddressDao.insertAddress(updatedAddress)
                Timber.d("選択住所の状態をクリアしました: ${selectedAddress.addressName}")
            } else {
                Timber.d("クリアする選択住所がありません")
            }
        } catch (e: Exception) {
            Timber.e(e, "選択住所のクリアに失敗しました")
            throw e
        }
    }

//    suspend fun getAddressCount(): Int {
//        return savedAddressDao.getAddressCount()
//    }
}