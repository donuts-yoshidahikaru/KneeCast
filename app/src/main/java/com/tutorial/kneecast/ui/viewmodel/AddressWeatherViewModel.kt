package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.remote.GeocodeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class AddressWeatherViewModel : ViewModel() {
    private val geocodeRepository = GeocodeRepository()
    
    // 住所入力テキスト
    private val _addressInput = MutableStateFlow("")
    val addressInput: StateFlow<String> = _addressInput
    
    // 候補住所リスト
    private val _addressSuggestions = MutableStateFlow<List<Feature>>(emptyList())
    val addressSuggestions: StateFlow<List<Feature>> = _addressSuggestions
    
    // 選択された住所のリスト（最大5件）
    private val _selectedAddresses = MutableStateFlow<List<Feature>>(emptyList())
    val selectedAddresses: StateFlow<List<Feature>> = _selectedAddresses
    
    // 現在選択中の住所（天気表示用）
    private val _currentSelectedAddress = MutableStateFlow<Feature?>(null)
    val currentSelectedAddress: StateFlow<Feature?> = _currentSelectedAddress
    
    // 読み込み中状態
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        // 入力テキストの変更を監視し、デバウンス処理を行う
        viewModelScope.launch {
            addressInput
                .debounce(500) // 500ms間入力がなければ次の処理へ
                .filter { it.length >= 2 } // 2文字以上入力されたら
                .collect { address ->
                    fetchAddressSuggestions(address)
                }
        }
    }
    
    // 入力テキストを更新
    fun updateAddressInput(input: String) {
        _addressInput.value = input
    }
    
    // 候補住所を取得
    private suspend fun fetchAddressSuggestions(address: String) {
        _isLoading.value = true
        try {
            val response = geocodeRepository.getResponseFromAddress(address)
            _addressSuggestions.value = response?.Feature ?: emptyList()
        } catch (e: Exception) {
            _addressSuggestions.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
    
    // 住所を選択してリストに追加
    fun selectAddress(feature: Feature) {
        val currentList = _selectedAddresses.value.toMutableList()
        
        // すでに同じ住所が登録されていない場合かつ5件未満の場合のみ追加
        if (!currentList.any { it.Name == feature.Name } && currentList.size < 5) {
            currentList.add(feature)
            _selectedAddresses.value = currentList
            
            // 現在選択中の住所を設定（最新のものを表示）
            _currentSelectedAddress.value = feature
        }
        
        // 候補リストをクリア
        _addressSuggestions.value = emptyList()
        
        // 入力フィールドをクリア
        _addressInput.value = ""
    }
    
    // 住所を表示用に選択
    fun setCurrentAddress(feature: Feature) {
        _currentSelectedAddress.value = feature
    }
    
    // 住所を削除
    fun removeAddress(feature: Feature) {
        val currentList = _selectedAddresses.value.toMutableList()
        currentList.remove(feature)
        _selectedAddresses.value = currentList
        
        // 削除した住所が現在選択中だった場合、別の住所を選択
        if (_currentSelectedAddress.value == feature) {
            _currentSelectedAddress.value = currentList.lastOrNull()
        }
    }
} 