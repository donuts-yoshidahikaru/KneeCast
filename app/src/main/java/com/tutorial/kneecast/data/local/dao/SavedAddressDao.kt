package com.tutorial.kneecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tutorial.kneecast.data.local.entity.SavedAddress

@Dao
interface SavedAddressDao {
    @Query("SELECT * FROM saved_addresses ORDER BY created_at DESC")
    suspend fun getAllAddresses(): List<SavedAddress>
    
    @Query("SELECT * FROM saved_addresses WHERE is_selected = 1 LIMIT 1")
    suspend fun getSelectedAddress(): SavedAddress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: SavedAddress): Long
    
    @Query("DELETE FROM saved_addresses WHERE address_name = :addressName")
    suspend fun deleteAddressByName(addressName: String)
    
    @Query("SELECT * FROM saved_addresses WHERE address_name = :addressName LIMIT 1")
    suspend fun findAddressByName(addressName: String): SavedAddress?
    
    @Query("SELECT * FROM saved_addresses WHERE address_name = :addressName AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    suspend fun findAddressByNameAndCoordinates(addressName: String, latitude: Double, longitude: Double): SavedAddress?
    
    @Query("UPDATE saved_addresses SET is_selected = CASE WHEN id = :addressId THEN 1 ELSE 0 END")
    suspend fun updateSelectedAddress(addressId: Long)
}