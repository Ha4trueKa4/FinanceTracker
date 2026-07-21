package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Expense>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Expense>>> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val expenses = expenseRepository.getExpenses()
                _uiState.value = if (expenses.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Content(expenses)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    if (e is java.io.IOException) "Нет подключения к интернету" else (e.message ?: "Неизвестная ошибка")
                )
            }
        }
    }
}