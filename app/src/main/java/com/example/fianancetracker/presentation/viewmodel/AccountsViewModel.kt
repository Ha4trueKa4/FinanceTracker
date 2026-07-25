package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.repository.AccountRepository
import com.example.fianancetracker.presentation.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = AccountsUiState.Loading
            accountRepository.getAccounts()
                .onSuccess { accounts ->
                    _uiState.value = if (accounts.isEmpty()) {
                        AccountsUiState.Empty
                    } else {
                        AccountsUiState.Content(accounts)
                    }
                }
                .onFailure { _uiState.value = AccountsUiState.Error(it.toUserMessage()) }
        }
    }
}
