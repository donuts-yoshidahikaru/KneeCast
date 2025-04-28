package com.tutorial.kneecast.ui.components.addressWeather
    
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutorial.kneecast.data.repository.SavedAddressRepository
import com.tutorial.kneecast.ui.viewmodel.AddressWeatherViewModel

class AddressWeatherViewModelFactory(
    private val savedAddressRepository: SavedAddressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressWeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddressWeatherViewModel(savedAddressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}