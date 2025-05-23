package com.tutorial.kneecast.domain.entity

// Assuming an address has a name/alias and coordinates.
// This might need to be adjusted based on the actual structure in SavedAddressRepository.kt
data class SavedAddress(
    val id: Long, // Or String, depending on how it's stored
    val name: String,
    val address: String, // Full address string
    val coordinates: Coordinates
)
