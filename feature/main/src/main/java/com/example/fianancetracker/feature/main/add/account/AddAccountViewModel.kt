package com.example.fianancetracker.feature.main.add.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.feature.main.add.common.appendKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** ViewModel bottom-sheet'а добавления/редактирования счёта: состояние формы и сабмит. */
class AddAccountViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddAccountUiState())
    val state: StateFlow<AddAccountUiState> = _state.asStateFlow()

    // Кэш счетов из локального Flow — чтобы открыть редактирование без похода в сеть.
    private var cachedAccounts: List<Account> = emptyList()

    init {
        viewModelScope.launch {
            accountRepository.observeAccounts().collect { cachedAccounts = it }
        }
    }

    fun open() = _state.update { AddAccountUiState(visible = true) }

    fun openEdit(id: String) {
        val account = cachedAccounts.firstOrNull { it.id == id } ?: return
        _state.update {
            AddAccountUiState(
                visible = true,
                editingId = id,
                name = account.name,
                amountDigits = account.balance.toLong().toString().filter { c -> c.isDigit() },
                emoji = account.emoji.ifEmpty { ACCOUNT_EMOJIS.first() },
                currency = account.currency.ifEmpty { "RUB" },
            )
        }
    }

    fun close() = _state.update { AddAccountUiState() }

    fun onKey(key: String) = _state.update { it.copy(amountDigits = appendKey(it.amountDigits, key)) }

    fun onBackspace() = _state.update { it.copy(amountDigits = it.amountDigits.dropLast(1)) }

    fun onName(text: String) = _state.update { it.copy(name = text) }

    fun selectEmoji(emoji: String) = _state.update { it.copy(emoji = emoji) }

    fun openCurrencyPicker() = _state.update { it.copy(showCurrencyPicker = true) }

    fun dismissCurrencyPicker() = _state.update { it.copy(showCurrencyPicker = false) }

    fun selectCurrency(code: String) =
        _state.update { it.copy(currency = code, showCurrencyPicker = false) }

    fun confirm() {
        val current = _state.value
        if (current.submitting) return
        if (!current.isValid) {
            _state.update { it.copy(showErrors = true) }
            return
        }

        val name = current.name.trim()
        val emoji = current.emoji
        val balance = (current.amountDigits.toLongOrNull() ?: 0L).toDouble()

        _state.update { it.copy(submitting = true) }
        viewModelScope.launch {
            val result = current.editingId?.let { id ->
                accountRepository.updateAccount(id, name, emoji, balance, current.currency)
            } ?: accountRepository.createAccount(name, emoji, balance, current.currency)

            result
                // Счёт записан локально → Flow счетов обновит экран сам.
                .onSuccess { close() }
                .onFailure { _state.update { it.copy(submitting = false) } }
        }
    }
}
