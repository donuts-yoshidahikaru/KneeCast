package com.tutorial.kneecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tutorial.kneecast.data.model.Feature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * HorizontalPager のページ位置と選択住所を管理するだけの軽量 ViewModel
 */
class AddressPagerViewModel : ViewModel() {

    data class UiState(
        val addresses: List<Feature> = emptyList(),
        val selectedIndex: Int = 0               // HorizontalPager.currentPage
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    /** 親 Composable から毎回呼ぶ ― 差分があれば state を更新 */
    fun syncAddresses(
        addresses: List<Feature>,
        externallySelected: Feature?            // null の場合は「前回のまま」
    ) {
        if (addresses.isEmpty()) return

        _uiState.update { old ->
            // 外部から新しい selected が来たら、それを優先
            val newIndex = externallySelected
                ?.let { addresses.indexOf(it) }
                ?.takeIf { it >= 0 } ?: old.selectedIndex.coerceIn(addresses.indices)

            UiState(addresses, newIndex)
        }
    }

    /** Pager からページ変更イベントを受け取る */
    fun onPageChanged(newIndex: Int) = _uiState.update {
        it.copy(selectedIndex = newIndex)
    }
}