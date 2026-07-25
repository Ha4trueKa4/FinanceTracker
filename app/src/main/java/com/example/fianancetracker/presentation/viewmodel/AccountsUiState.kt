package com.example.fianancetracker.presentation.viewmodel

import com.example.fianancetracker.domain.account.Account

/** Состояние экрана счетов. */
sealed interface AccountsUiState {
    data object Loading : AccountsUiState
    data object Empty : AccountsUiState
    data class Error(val message: String) : AccountsUiState
    data class Content(val accounts: List<Account>) : AccountsUiState
}
