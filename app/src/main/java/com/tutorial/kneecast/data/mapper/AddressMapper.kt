package com.tutorial.kneecast.data.mapper

// Import aliases for clarity
import com.tutorial.kneecast.data.local.entity.SavedAddress as DbSavedAddress
import com.tutorial.kneecast.domain.entity.Coordinates as DomainCoordinates
import com.tutorial.kneecast.domain.entity.SavedAddress as DomainSavedAddress

object AddressMapper {

    fun mapDbEntityToDomain(dbEntity: DbSavedAddress): DomainSavedAddress {
        return DomainSavedAddress(
            id = dbEntity.id,
            name = dbEntity.addressName,
            address = dbEntity.addressName, // Map addressName to both name and address in domain
            coordinates = DomainCoordinates(
                latitude = dbEntity.latitude,
                longitude = dbEntity.longitude
            )
            // dbEntity.isSelected and dbEntity.createdAt are not mapped to the domain entity
        )
    }

    fun mapDomainToDbEntity(
        domainEntity: DomainSavedAddress,
        isSelected: Boolean? = null
    ): DbSavedAddress {
        return DbSavedAddress(
            id = domainEntity.id,
            addressName = domainEntity.name, // domainEntity.address is not persisted if different from name
            latitude = domainEntity.coordinates.latitude,
            longitude = domainEntity.coordinates.longitude,
            isSelected = isSelected ?: false
            // createdAt is handled by DbSavedAddress default
        )
    }

    fun mapDbEntityListToDomainList(dbEntities: List<DbSavedAddress>): List<DomainSavedAddress> {
        return dbEntities.map { mapDbEntityToDomain(it) }
    }
    
    // A mapDomainListToDbList could be added if needed, for example:
    // fun mapDomainListToDbList(domainEntities: List<DomainSavedAddress>, isSelected: Boolean? = null): List<DbSavedAddress> {
    //     return domainEntities.map { mapDomainToDbEntity(it, isSelected) }
    // }
}