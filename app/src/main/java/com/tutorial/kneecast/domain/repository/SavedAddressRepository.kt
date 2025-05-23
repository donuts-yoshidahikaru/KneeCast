package com.tutorial.kneecast.domain.repository

import com.tutorial.kneecast.domain.common.Result
import com.tutorial.kneecast.domain.entity.SavedAddress
import kotlinx.coroutines.flow.Flow // Assuming it might use Flow for reactive updates

interface SavedAddressRepository {
    suspend fun addAddress(address: SavedAddress): Result<Unit>
    suspend fun getSavedAddresses(): Flow<List<SavedAddress>> // Or suspend fun getSavedAddresses(): Result<List<SavedAddress>>
    suspend fun deleteAddress(addressId: Long): Result<Unit> // Or String for addressId
    // Add other methods like updateAddress if needed
}
