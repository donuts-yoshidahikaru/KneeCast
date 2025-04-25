package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.remote.GeocodeRepository
import com.tutorial.kneecast.data.repository.SavedAddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressWeatherViewModel(private val savedAddressRepository: SavedAddressRepository) : ViewModel() {
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
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
        viewModelScope.launch {
            loadSavedAddresses()
        }
    }
    
    // エラーメッセージをクリア
    fun clearError() {
        _error.value = null
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentList = _selectedAddresses.value.toMutableList()
                // 同じ住所が登録されておらず、かつ5件未満の場合のみ追加
                if (!currentList.any { it.Name == feature.Name } && currentList.size < 5) {
                    // データベースに保存し、選択状態にする
                    savedAddressRepository.saveAddress(feature, true)
                    
                    // データベースから最新の状態を読み込む
                    loadSavedAddresses()
                    
                    // 候補リストと入力フィールドをクリア
                    _addressSuggestions.value = emptyList()
                    _addressInput.value = ""
                } else {
                    // エラーメッセージを設定
                    if (currentList.any { it.Name == feature.Name }) {
                        _error.value = "同じ住所は登録できません"
                    } else {
                        _error.value = "登録できる住所は5件までです"
                    }
                }
            } catch (e: Exception) {
                _error.value = "住所の保存に失敗しました: ${e.localizedMessage}"
                Timber.e(e, "Failed to save address: ${feature.Name}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 住所を表示用に選択（データベースの選択状態も更新）
    fun setCurrentAddress(feature: Feature) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 選択する住所が現在のリストに存在するか確認
                if (_selectedAddresses.value.any { it.Name == feature.Name }) {
                    // データベースの選択状態を更新
                    savedAddressRepository.updateSelectedAddress(feature)
                    
                    // UI状態を更新
                    _currentSelectedAddress.value = feature
                    
                    // リストの選択状態も更新（視覚的フィードバック用）
                    refreshAddressListSelection(feature)
                    
                    Timber.d("選択住所を ${feature.Name} に更新しました")
                } else {
                    _error.value = "指定された住所がリストに存在しません"
                    Timber.w("Address not found in list for selection: ${feature.Name}")
                }
            } catch (e: Exception) {
                _error.value = "選択住所の更新に失敗しました: ${e.localizedMessage}"
                Timber.e(e, "Failed to set current address: ${feature.Name}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // リスト内の住所の選択状態を更新（視覚的フィードバック用）
    private fun refreshAddressListSelection(selectedFeature: Feature) {
        // 現在のリストをそのまま維持することで再描画を最小限に
        // 実際の選択状態はcurrentSelectedAddressで管理されているため
        // このメソッドは将来的に拡張するための準備
    }
    
    // 住所を削除
    fun removeAddress(feature: Feature) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 削除する住所が現在のリストに存在するか確認
                if (_selectedAddresses.value.any { it.Name == feature.Name }) {
                    // データベースから削除
                    savedAddressRepository.deleteAddress(feature)
                    
                    // UIの状態を更新
                    val currentList = _selectedAddresses.value.toMutableList()
                    currentList.removeIf { it.Name == feature.Name }
                    _selectedAddresses.value = currentList
                    
                    // 削除した住所が現在選択中だった場合、別の住所を選択
                    if (_currentSelectedAddress.value?.Name == feature.Name) {
                        val nextAddress = currentList.lastOrNull()
                        if (nextAddress != null) {
                            // 次の住所を選択状態にする（DB更新も行う）
                            setCurrentAddress(nextAddress)
                        } else {
                            // リストが空になった場合は選択解除
                            _currentSelectedAddress.value = null
                        }
                    }
                    
                    Timber.d("住所 ${feature.Name} を削除しました")
                } else {
                    _error.value = "指定された住所がリストに存在しません"
                    Timber.w("Address not found in list for removal: ${feature.Name}")
                }
            } catch (e: Exception) {
                _error.value = "住所の削除に失敗しました: ${e.localizedMessage}"
                Timber.e(e, "Failed to remove address: ${feature.Name}")
                
                // エラーが発生した場合は、データベースから再度読み込んで整合性を確保
                loadSavedAddresses()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadSavedAddresses() {
        _isLoading.value = true
        try {
            val addresses = savedAddressRepository.getAllAddresses()
            
            // 既に表示中の住所との重複を防止（同じ住所名の住所を除外）
            val uniqueAddresses = addresses.distinctBy { it.Name }
            
            if (uniqueAddresses.size != addresses.size) {
                Timber.w("重複した住所が検出されました。重複を除外します: ${addresses.size} → ${uniqueAddresses.size}")
            }
            
            _selectedAddresses.value = uniqueAddresses

            // 選択中の住所を復元
            savedAddressRepository.getSelectedAddress()?.let { address ->
                _currentSelectedAddress.value = address
                Timber.d("選択中の住所を復元: ${address.Name}")
            }
        } catch (e: Exception) {
            _error.value = "保存された住所の読み込みに失敗しました: ${e.localizedMessage}"
            Timber.e(e, "Failed to load saved addresses")
        } finally {
            _isLoading.value = false
        }
    }
} 