package com.example.fianancetracker.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.domain.analytics.TransactionType
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.domain.repository.AnalyticsRepository
import com.example.fianancetracker.domain.transaction.Transaction
import com.example.fianancetracker.presentation.components.common.PieChartSlice
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(AnalyticsFilter())
    val filter: StateFlow<AnalyticsFilter> = _filter.asStateFlow()

    private val _state = MutableStateFlow<UiState<AnalyticsState>>(UiState.Loading)
    val state: StateFlow<UiState<AnalyticsState>> = _state.asStateFlow()

    private var allExpenses: List<Expense> = emptyList()
    private var allIncomes: List<Income> = emptyList()
    private var allAccounts: List<Account> = emptyList()
    private var allCategories: List<Category> = emptyList()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    init {
        loadData()
    }

    fun updateFilter(newFilter: AnalyticsFilter) {
        _filter.value = newFilter
        applyFilters()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val data = analyticsRepository.getAnalyticsData()
                allExpenses = data.expenses
                allIncomes = data.incomes
                allAccounts = data.accounts
                allCategories = data.categories
                applyFilters()
            } catch (e: Exception) {
                _state.value = UiState.Error(
                    if (e is IOException) "Нет подключения к интернету" else "Неизвестная ошибка"
                )
            }
        }
    }

    private fun applyFilters() {
        val currentFilter = _filter.value
        val filteredExpenses = allExpenses.filter { matchesFilter(it, currentFilter) }
        val filteredIncomes = allIncomes.filter { matchesFilter(it, currentFilter) }

        val combinedItems: List<Transaction> = when (currentFilter.type) {
            TransactionType.EXPENSE -> filteredExpenses
            TransactionType.INCOME -> filteredIncomes
            TransactionType.ALL -> filteredExpenses + filteredIncomes
        }.sortedByDescending { parseDate(it.date) ?: LocalDate.MIN }

        val totalAmount = combinedItems.sumOf { it.amount }

        val categoryTotals = combinedItems
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }

        val slices = categoryTotals
            .filter { it.value > 0 }
            .map { (category, amount) ->
                PieChartSlice(
                    label = category.name,
                    emoji = category.emoji,
                    amount = amount,
                    color = Color.Unspecified,
                    percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
                )
            }

        val items = combinedItems.map { transaction ->
            ListItemModel(
                id = transaction.id,
                lead = ListItemModel.Lead(emoji = transaction.category.emoji),
                content = ListItemModel.Content(
                    title = transaction.title,
                    subtitle = transaction.accountName
                ),
                trail = ListItemModel.Trail(
                    amount = toThousand(transaction.amount),
                    isPositive = transaction.amount >= 0
                )
            )
        }

        _state.value = UiState.Content(
            AnalyticsState(
                accounts = allAccounts,
                categories = allCategories,
                slices = slices,
                items = items,
                totalAmount = totalAmount,
                filter = currentFilter
            )
        )
    }

    private fun matchesFilter(transaction: Transaction, filter: AnalyticsFilter): Boolean {
        if (filter.selectedCategoryIds.isNotEmpty() && transaction.category.id !in filter.selectedCategoryIds) {
            return false
        }
        if (filter.selectedAccountName != null && transaction.accountName != filter.selectedAccountName) {
            return false
        }
        if (!isDateInRange(transaction.date, filter.effectiveStartDate, filter.effectiveEndDate)) {
            return false
        }
        return true
    }

    private fun isDateInRange(dateStr: String, start: LocalDate, end: LocalDate): Boolean {
        return try {
            val date = LocalDate.parse(dateStr, dateFormatter)
            !date.isBefore(start) && !date.isAfter(end)
        } catch (_: Exception) {
            true
        }
    }

    private fun parseDate(dateStr: String): LocalDate? = try {
        LocalDate.parse(dateStr, dateFormatter)
    } catch (_: Exception) {
        null
    }
}