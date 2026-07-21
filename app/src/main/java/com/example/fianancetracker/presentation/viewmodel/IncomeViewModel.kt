package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeViewModel(
    private val incomeRepository: IncomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Income>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Income>>> = _uiState.asStateFlow()

    private val _errorEvent = MutableStateFlow<String?>(null)
    val errorEvent: StateFlow<String?> = _errorEvent.asStateFlow()

    init {
        loadIncomes()
    }

    fun loadIncomes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val incomes = incomeRepository.getIncomes()
                _uiState.value = if (incomes.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Content(incomes)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Неизвестная ошибка")
                _errorEvent.value = e.message ?: "Неизвестная ошибка"
            }
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }
}