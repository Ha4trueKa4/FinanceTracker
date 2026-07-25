package com.example.fianancetracker.presentation.viewmodel

import com.example.fianancetracker.domain.transaction.Transaction

/** Состояние экрана расходов/доходов. Тип данных фиксирован — никаких дженериков. */
sealed interface TransactionsUiState {
    data object Loading : TransactionsUiState
    data object Empty : TransactionsUiState
    data class Error(val message: String) : TransactionsUiState
    data class Content(val items: List<Transaction>) : TransactionsUiState
}
