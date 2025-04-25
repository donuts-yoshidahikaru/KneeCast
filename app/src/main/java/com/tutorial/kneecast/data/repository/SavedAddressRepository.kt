package com.tutorial.kneecast.data.repository

import com.tutorial.kneecast.data.local.dao.SavedAddressDao
import com.tutorial.kneecast.data.mapper.AddressMapper
import com.tutorial.kneecast.data.model.Feature

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
        return savedAddressDao.insertAddress(AddressMapper.fromFeature(feature, isSelected))
    }

    suspend fun deleteAddress(feature: Feature) {
        // 既存の住所を検索して削除
        val savedAddress = AddressMapper.fromFeature(feature)
        savedAddressDao.deleteAddress(savedAddress)
    }

    suspend fun updateSelectedAddress(feature: Feature) {
        val savedAddress = AddressMapper.fromFeature(feature, true)
        val id = savedAddressDao.insertAddress(savedAddress)
        savedAddressDao.updateSelectedAddress(id)
    }

    suspend fun getAddressCount(): Int {
        return savedAddressDao.getAddressCount()
    }
}