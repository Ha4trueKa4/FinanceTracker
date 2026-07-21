package com.example.fianancetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.domain.repository.AccountRepository
import com.example.fianancetracker.domain.repository.ExpenseRepository
import com.example.fianancetracker.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(AnalyticsFilter())
    val filter: StateFlow<AnalyticsFilter> = _filter.asStateFlow()

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun updateFilter(newFilter: AnalyticsFilter) {
        _filter.value = newFilter
        applyFilters()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val accounts = accountRepository.getAccounts()
                val expenses = expenseRepository.getExpenses()
                val incomes = incomeRepository.getIncomes()

                val allCategories = (expenses.map { it.category } + incomes.map { it.category })
                    .distinctBy { it.id }

                _state.value = _state.value.copy(
                    accounts = accounts,
                    categories = allCategories,
                    allExpenses = expenses,
                    allIncomes = incomes,
                    isLoading = false
                )
                applyFilters()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = if (e is java.io.IOException) "Нет подключения к интернету" else "Неизвестная ошибка"
                )
            }
        }
    }

    private fun applyFilters() {
        val currentFilter = _filter.value
        val currentState = _state.value

        val filteredExpenses = currentState.allExpenses.filter { expense ->
            matchesFilter(expense.date, expense.category, expense.accountName, currentFilter)
        }
        val filteredIncomes = currentState.allIncomes.filter { income ->
            matchesFilter(income.date, income.category, income.accountName, currentFilter)
        }

        _state.value = currentState.copy(
            filteredExpenses = filteredExpenses,
            filteredIncomes = filteredIncomes,
            filter = currentFilter
        )
    }

    private fun matchesFilter(
        date: String,
        category: Category,
        accountName: String,
        filter: AnalyticsFilter
    ): Boolean {
        if (filter.selectedCategoryIds.isNotEmpty() && category.id !in filter.selectedCategoryIds) {
            return false
        }
        if (filter.selectedAccountName != null && accountName != filter.selectedAccountName) {
            return false
        }
        if (!isDateInRange(date, filter.effectiveStartDate, filter.effectiveEndDate)) {
            return false
        }
        return true
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private fun isDateInRange(dateStr: String, start: LocalDate, end: LocalDate): Boolean {
        return try {
            val date = LocalDate.parse(dateStr, dateFormatter)
            !date.isBefore(start) && !date.isAfter(end)
        } catch (_: Exception) {
            true
        }
    }
}