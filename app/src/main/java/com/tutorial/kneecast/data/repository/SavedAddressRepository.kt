package com.tutorial.kneecast.data.repository

import com.tutorial.kneecast.data.local.dao.SavedAddressDao
import com.tutorial.kneecast.domain.repository.SavedAddressRepository as DomainSavedAddressRepository
import com.tutorial.kneecast.domain.entity.SavedAddress as DomainSavedAddress
// Removed DomainCoordinates import as it's part of DomainSavedAddress
// import com.tutorial.kneecast.data.local.entity.SavedAddress as DbSavedAddress // Alias for DB entity - No longer needed here if mappers are used
import com.tutorial.kneecast.data.mapper.AddressMapper // Import the finalized AddressMapper
import com.tutorial.kneecast.domain.common.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.flow // Not used as DAO returns Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

/**
 * 保存された住所を管理するリポジトリ (Domain Interface Implementation)
 */
class SavedAddressRepository(
    private val savedAddressDao: SavedAddressDao
) : DomainSavedAddressRepository {
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val tag = "Data-SavedAddressRepo"

    // Temporary private mappers are removed.

    override suspend fun addAddress(address: DomainSavedAddress): Result<Unit> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                Timber.tag(tag).d("Adding address: ${address.name}")
                // Check if address with same name and coordinates already exists
                val existingDbAddress = savedAddressDao.findAddressByNameAndCoordinates(
                    address.name,
                    address.coordinates.latitude,
                    address.coordinates.longitude
                )

                if (existingDbAddress != null) {
                    // Address exists, update it.
                    // Use AddressMapper.mapDomainToDbEntity. The isSelected parameter defaults to false in the mapper
                    // if not specified, which is suitable here. The ID from existingDbAddress must be preserved.
                    val dbEntityToUpdate = AddressMapper.mapDomainToDbEntity(address).copy(id = existingDbAddress.id)
                    savedAddressDao.insertAddress(dbEntityToUpdate) // insert acts as upsert due to onConflictStrategy
                    Timber.tag(tag).d("Updated existing address: ${address.name}")
                } else {
                    // New address, insert it.
                    // Use AddressMapper.mapDomainToDbEntity.
                    // The ID should be 0 for Room to auto-generate if that's the strategy.
                    // The isSelected parameter defaults to false in the mapper.
                    val dbEntity = AddressMapper.mapDomainToDbEntity(address).copy(id = 0) // Ensure ID is 0 for auto-generation
                    savedAddressDao.insertAddress(dbEntity)
                    Timber.tag(tag).d("Inserted new address: ${address.name}")
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Timber.tag(tag).e(e, "Error adding address: ${address.name}")
                Result.Error("Failed to add address: ${e.message}", e)
            }
        }
    }

    override suspend fun getSavedAddresses(): Flow<List<DomainSavedAddress>> {
        return savedAddressDao.getAllAddressesFlow() // DAO returns Flow<List<DbSavedAddress>>
            .map { dbAddressList ->
                // Use AddressMapper to convert the list of DB entities to domain entities
                // AddressMapper.mapDbEntityListToDomainList(dbAddressList)
                // Or, map individually:
                dbAddressList.map { dbEntity -> AddressMapper.mapDbEntityToDomain(dbEntity) }
            }
    }

    override suspend fun deleteAddress(address: DomainSavedAddress): Result<Unit> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                Timber.tag(tag).d("Deleting address by name: ${address.name}")
                // Assumes SavedAddressDao has deleteAddressByName(addressName: String)
                // And that address.name corresponds to addressName in the DB
                savedAddressDao.deleteAddressByName(address.name)
                Result.Success(Unit)
            } catch (e: Exception) {
                Timber.tag(tag).e(e, "Error deleting address: ${address.name}")
                Result.Error("Failed to delete address: ${e.message}", e)
            }
        }
    }
}