package com.tutorial.kneecast.data.repository

import com.tutorial.kneecast.data.local.dao.SavedAddressDao
import com.tutorial.kneecast.data.mapper.AddressMapper
import com.tutorial.kneecast.data.model.Feature
import timber.log.Timber

class SavedAddressRepository(
    private val savedAddressDao: SavedAddressDao
) {
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
        val (longitude, latitude) = feature.Geometry.Coordinates
            .split(",")
            .map { it.trim().toDouble() }
            
        val existingAddress = savedAddressDao.findAddressByNameAndCoordinates(
            feature.Name, latitude, longitude
        )
        
        // 既存の住所が見つかった場合は、それを更新
        if (existingAddress != null) {
            Timber.d("既存の住所を更新: ${feature.Name}")
            val updatedAddress = existingAddress.copy(isSelected = isSelected)
            val id = savedAddressDao.insertAddress(updatedAddress)
            
            if (isSelected) {
                savedAddressDao.updateSelectedAddress(id)
            }
            
            return id
        }
        
        // 新規住所の保存
        Timber.d("新規住所を保存: ${feature.Name}")
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
            Timber.d("住所を削除: ${feature.Name}")
            savedAddressDao.deleteAddressByName(feature.Name)
        } catch (e: Exception) {
            Timber.e(e, "住所の削除に失敗: ${feature.Name}")
            throw e
        }
    }

    suspend fun updateSelectedAddress(feature: Feature) {
        try {
            val savedAddress = AddressMapper.fromFeature(feature, true)
            
            // 既存の住所を検索
            val existingAddress = savedAddressDao.findAddressByName(feature.Name)
            
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
            Timber.e(e, "選択住所の更新に失敗: ${feature.Name}")
            throw e
        }
    }

    suspend fun getAddressCount(): Int {
        return savedAddressDao.getAddressCount()
    }
}