package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.repository.TransactionRepository
import com.example.fianancetracker.presentation.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Общая VM для экранов расходов и доходов — они отличаются только флагом [isIncome].
 */
class TransactionsViewModel(
    private val isIncome: Boolean,
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Loading)
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = TransactionsUiState.Loading
            repository.getTransactions(isIncome)
                .onSuccess { items ->
                    _uiState.value = if (items.isEmpty()) {
                        TransactionsUiState.Empty
                    } else {
                        TransactionsUiState.Content(items)
                    }
                }
                .onFailure { _uiState.value = TransactionsUiState.Error(it.toUserMessage()) }
        }
    }
}
