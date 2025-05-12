package com.tutorial.kneecast.ui.components.addressWeather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.repository.factory.SavedAddressRepositoryFactory
import com.tutorial.kneecast.ui.viewmodel.AddressWeatherViewModel
import timber.log.Timber

/**
 * 住所検索画面
 * 
 * メイン画面から遷移し、住所を検索して選択すると元の画面に戻る
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchScreen(
    onNavigateBack: () -> Unit,
    onAddressSelected: (Feature) -> Unit
) {
    val context = LocalContext.current
    
    // SavedAddressRepositoryを初期化
    val savedAddressRepository = remember { 
        SavedAddressRepositoryFactory.create(context) 
    }
    
    // ViewModelを初期化
    val viewModel: AddressWeatherViewModel = viewModel(
        factory = AddressWeatherViewModelFactory(savedAddressRepository)
    )
    
    // StateFlowをStateとして収集
    val addressInput by viewModel.addressInput.collectAsState()
    val suggestions by viewModel.addressSuggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // エラーメッセージの表示
    LaunchedEffect(error) {
        error?.let {
            Timber.e("住所検索エラー: $it")
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("住所検索") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 住所入力フィールド
            OutlinedTextField(
                value = addressInput,
                onValueChange = { viewModel.updateAddressInput(it) },
                label = { Text("住所を入力") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 候補がないときのメッセージ
            if (addressInput.length >= 2 && suggestions.isEmpty() && !isLoading) {
                Text(
                    text = "検索結果がありません",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // 候補住所リスト
            if (suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = { 
                                // 住所を選択してメイン画面に戻る
                                onAddressSelected(suggestion)
                                onNavigateBack()
                            }
                        )
                    }
                }
            } else {
                // 検索ヒントの表示
                if (addressInput.length < 2) {
                    Text(
                        text = "2文字以上入力して住所を検索してください",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        // 読み込み中インジケータ
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(10f)
            )
        }
    }
} 