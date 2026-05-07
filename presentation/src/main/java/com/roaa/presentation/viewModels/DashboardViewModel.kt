package com.roaa.presentation.viewModels


import androidx.lifecycle.*
import com.roaa.domain.usecase.*
import com.roaa.presentation.utils.models.PasswordStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTotalPasswordCountUseCase: GetTotalPasswordCountUseCase,
    private val getReusedPasswordCountUseCase: GetReusedPasswordCountUseCase,
    private val getWeakPasswordCountUseCase: GetWeakPasswordCountUseCase,
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

}