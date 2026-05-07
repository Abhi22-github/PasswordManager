package com.roaa.presentation.viewModels

import androidx.lifecycle.*
import com.roaa.domain.usecase.SearchBrandsUseCase
import com.roaa.presentation.utils.models.BrandSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class BrandInfoViewModel @Inject constructor(
    private val searchBrands: SearchBrandsUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _queryUiState = MutableStateFlow(BrandSearchUiState())
    val queryUiState: StateFlow<BrandSearchUiState> = _queryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)              // wait for typing pause
                .distinctUntilChanged()
                .flatMapLatest { q ->       // cancel previous search
                    flow {
                        if (q.length < 2) {
                            emit(BrandSearchUiState())
                        } else {
                            emit(BrandSearchUiState(isLoading = true))
                            val result = searchBrands(q)
                            emit(
                                result.fold(
                                    onSuccess = { BrandSearchUiState(suggestions = it) },
                                    onFailure = { BrandSearchUiState(error = it.message) }
                                )
                            )
                        }
                    }
                }
                .distinctUntilChanged { old, new ->
                    old.suggestions.map { it.domain } == new.suggestions.map { it.domain } &&
                            old.isLoading == new.isLoading
                }
                .collect { _queryUiState.value = it }
        }
    }

    fun clearSuggestions(){
        _queryUiState.value = BrandSearchUiState(suggestions = listOf())
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }
}
