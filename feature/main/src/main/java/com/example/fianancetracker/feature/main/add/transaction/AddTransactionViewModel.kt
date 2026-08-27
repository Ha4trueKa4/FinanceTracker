package com.example.fianancetracker.feature.main.add.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.CategoryRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import com.example.fianancetracker.feature.main.add.common.appendKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** ViewModel bottom-sheet'а добавления/редактирования операции: состояние формы и сабмит. */
class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionUiState())
    val state: StateFlow<AddTransactionUiState> = _state.asStateFlow()

    fun open(isIncome: Boolean) {
        _state.update { it.copy(visible = true, editingId = null, isIncome = isIncome) }
        viewModelScope.launch { loadDictionariesIfNeeded() }
    }

    fun openEdit(id: String) {
        _state.update {
            AddTransactionUiState(
                visible = true,
                loading = true,
                editingId = id,
                categories = it.categories,
                accounts = it.accounts,
            )
        }
        viewModelScope.launch {
            loadDictionariesIfNeeded()
            transactionRepository.getTransactionDetails(id)
                .onSuccess { details ->
                    _state.update {
                        val income = it.categories
                            .firstOrNull { category -> category.id == details.categoryId }
                            ?.isIncome ?: it.isIncome
                        it.copy(
                            loading = false,
                            isIncome = income,
                            selectedCategoryId = details.categoryId,
                            selectedAccountId = details.accountId.toString(),
                            amountDigits = details.amount.toLong().toString(),
                            comment = details.comment.orEmpty(),
                            date = details.date,
                            time = details.time,
                        )
                    }
                }
                .onFailure { close() }
        }
    }

    fun close() {
        _state.update { AddTransactionUiState(categories = it.categories, accounts = it.accounts) }
    }

    private suspend fun loadDictionariesIfNeeded() {
        if (_state.value.categories.isNotEmpty() && _state.value.accounts.isNotEmpty()) return
        _state.update { it.copy(loading = true) }
        val categories = categoryRepository.getCategories().getOrDefault(emptyList())
        val accounts = accountRepository.getAccounts().getOrDefault(emptyList())
        _state.update { it.copy(loading = false, categories = categories, accounts = accounts) }
    }

    fun onKey(key: String) = _state.update { it.copy(amountDigits = appendKey(it.amountDigits, key)) }

    fun onBackspace() = _state.update { it.copy(amountDigits = it.amountDigits.dropLast(1)) }

    fun onComment(text: String) = _state.update { it.copy(comment = text) }

    fun openPicker(picker: AddPicker) = _state.update { it.copy(openPicker = picker) }

    fun dismissPicker() = _state.update { it.copy(openPicker = AddPicker.NONE) }

    fun selectCategory(id: Long) =
        _state.update { it.copy(selectedCategoryId = id, openPicker = AddPicker.NONE) }

    fun selectAccount(id: String) =
        _state.update { it.copy(selectedAccountId = id, openPicker = AddPicker.NONE) }

    fun selectDate(date: LocalDate) =
        _state.update { it.copy(date = date, openPicker = AddPicker.NONE) }

    fun selectTime(time: LocalTime) =
        _state.update { it.copy(time = time, openPicker = AddPicker.NONE) }

    fun confirm() {
        val current = _state.value
        if (current.submitting) return
        if (!current.isValid) {
            _state.update { it.copy(showErrors = true) }
            return
        }

        val draft = buildDraft(current)
        _state.update { it.copy(submitting = true) }
        viewModelScope.launch {
            submit(current.editingId, draft)
                // Локальная запись уже в Room → Flow списка переэмитит сам, ручной reload не нужен.
                .onSuccess { close() }
                .onFailure { _state.update { it.copy(submitting = false) } }
        }
    }

    private fun buildDraft(state: AddTransactionUiState) = TransactionDraft(
        accountId = state.selectedAccountId!!.toLong(),
        categoryId = state.selectedCategoryId!!,
        amount = (state.amountDigits.toLongOrNull() ?: 0L).toDouble(),
        transactionDate = toIsoInstant(state.date, state.time),
        comment = state.comment.trim(),
    )

    private suspend fun submit(editingId: String?, draft: TransactionDraft): Result<Unit> =
        if (editingId != null) transactionRepository.updateTransaction(editingId, draft)
        else transactionRepository.createTransaction(draft)

    private fun toIsoInstant(date: LocalDate, time: LocalTime): String =
        isoUtcMillisFormatter.format(
            LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant()
        )

    private companion object {
        private val isoUtcMillisFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
    }
}
