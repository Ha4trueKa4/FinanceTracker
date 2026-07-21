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
                _uiState.value = UiState.Error(
                    if (e is java.io.IOException) "Нет подключения к интернету" else (e.message ?: "Неизвестная ошибка")
                )
            }
        }
    }
}