package com.roaa.presentation.viewModels


import androidx.lifecycle.*
import com.roaa.domain.model.Credentials
import com.roaa.domain.usecase.*
import com.roaa.presentation.utils.models.PasswordStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PasswordStatViewModel @Inject constructor(
    private val getTotalPasswordCountUseCase: GetTotalPasswordCountUseCase,
    private val getReusedPasswordCountUseCase: GetReusedPasswordCountUseCase,
    private val getWeakPasswordCountUseCase: GetWeakPasswordCountUseCase,
    private val getAllPasswordsUseCase: GetAllPasswordsUseCase,
) : ViewModel() {

    val passwordStat: StateFlow<PasswordStats> = combine(
        getTotalPasswordCountUseCase(),
        getReusedPasswordCountUseCase(),
        getWeakPasswordCountUseCase()
    ) { total, reused, weak ->
        PasswordStats(total, 0, reused, 0, weak)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PasswordStats(0, 0, 0, 0, 0)
    )

    private val allPasswords: StateFlow<List<Credentials>> = getAllPasswordsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val weakPasswords: StateFlow<List<Credentials>> = allPasswords
        .map { list -> list.filter { it.strength < 0.5f } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val reusedPasswords: StateFlow<List<Credentials>> = allPasswords
        .map { list ->
            val groups = list.groupBy { it.password }
            groups.values.filter { it.size > 1 }.flatten()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val compromisedPasswords: StateFlow<List<Credentials>> = MutableStateFlow(emptyList<Credentials>()).asStateFlow()
}