package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Account>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Account>>> = _uiState.asStateFlow()

    private val _errorEvent = MutableStateFlow<String?>(null)
    val errorEvent: StateFlow<String?> = _errorEvent.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val accounts = accountRepository.getAccounts()
                _uiState.value = if (accounts.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Content(accounts)
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