package com.roaa.presentation.viewModels

import androidx.lifecycle.*
import com.roaa.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val addPassword: AddPasswordUseCase,

) : ViewModel() {

}