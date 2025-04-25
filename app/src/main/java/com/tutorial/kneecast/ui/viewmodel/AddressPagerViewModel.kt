package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorial.kneecast.BuildConfig
import com.tutorial.kneecast.data.model.Feature
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressPagerViewModel : ViewModel() {

    data class UiState(
        val addresses: List<Feature> = emptyList(),
        val selectedIndex: Int = 0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed interface UiEvent {
        data class ShowToast(val message: String) : UiEvent
    }

    private val _event = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<UiEvent> = _event.asSharedFlow()

    fun syncAddresses(
        addresses: List<Feature>,
        externallySelected: Feature?
    ) {
        if (addresses.isEmpty()) return

        val oldState = _uiState.value
        val newIndex = externallySelected
            ?.let { addresses.indexOf(it) }
            ?.takeIf { it >= 0 }
            ?: oldState.selectedIndex.coerceIn(addresses.indices)

        if (addresses != oldState.addresses || newIndex != oldState.selectedIndex) {
            _uiState.value = UiState(addresses, newIndex)
            Timber.d("表示住所を ${addresses[newIndex].Name} に変更")
            if (oldState.addresses.isNotEmpty()) {
                postEvent(UiEvent.ShowToast("表示住所を ${addresses[newIndex].Name} に変更"))
            }
        }
    }

    fun onPageChanged(newIndex: Int) {
        if (newIndex == _uiState.value.selectedIndex) return

        _uiState.update { it.copy(selectedIndex = newIndex) }
        val name = _uiState.value.addresses[newIndex].Name
        Timber.d("選択住所を $name に変更")
    }

    private fun postEvent(event: UiEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}