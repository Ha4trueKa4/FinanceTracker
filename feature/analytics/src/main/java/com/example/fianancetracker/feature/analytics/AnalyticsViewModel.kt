package com.example.fianancetracker.feature.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.analytics.Period
import com.example.fianancetracker.core.domain.analytics.TransactionType
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.feature.analytics.components.PieChartSlice
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.core.ui.utils.toThousand
import com.example.fianancetracker.core.ui.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(AnalyticsFilter())
    val filter: StateFlow<AnalyticsFilter> = _filter.asStateFlow()

    private val _state = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

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
            _state.value = AnalyticsUiState.Loading
            analyticsRepository.getAnalyticsData()
                .onSuccess { data ->
                    allExpenses = data.expenses
                    allIncomes = data.incomes
                    allAccounts = data.accounts
                    allCategories = data.categories
                    applyFilters()
                }
                .onFailure { _state.value = AnalyticsUiState.Error(it.toUserMessage()) }
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
                val percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
                PieChartSlice(
                    label = category.name,
                    color = sliceColor(category.name),
                    percentage = percentage,
                    amountText = toThousand(amount),
                    percentText = "${"%.0f".format(percentage)}%",
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

        _state.value = AnalyticsUiState.Content(
            AnalyticsState(
                accounts = allAccounts,
                categories = allCategories,
                slices = slices,
                items = items,
                totalFormatted = toThousand(totalAmount),
                typeBadge = typeBadge(currentFilter.type),
                periodBadge = periodBadge(currentFilter),
                categoriesBadge = categoriesBadge(currentFilter),
                accountBadge = currentFilter.selectedAccountName ?: "Все счета",
                customRangeLabel = customRangeLabel(currentFilter),
                filter = currentFilter,
            )
        )
    }

    private fun sliceColor(label: String): Color {
        val hue = ((label.hashCode() and Int.MAX_VALUE) % 360).toFloat()
        return Color.hsl(hue, 0.6f, 0.55f)
    }

    private fun typeBadge(type: TransactionType): String = when (type) {
        TransactionType.EXPENSE -> "Расходы"
        TransactionType.INCOME -> "Доходы"
        TransactionType.ALL -> "Всё"
    }

    private fun periodBadge(filter: AnalyticsFilter): String = when (filter.period) {
        Period.WEEK -> "За неделю"
        Period.MONTH -> "За месяц"
        Period.QUARTER -> "За квартал"
        Period.YEAR -> "За год"
        Period.CUSTOM -> {
            val start = filter.customStartDate
            val end = filter.customEndDate
            if (start != null && end != null) {
                "${start.format(dateFormatter)} – ${end.format(dateFormatter)}"
            } else {
                "Произвольный"
            }
        }
    }

    private fun categoriesBadge(filter: AnalyticsFilter): String =
        if (filter.selectedCategoryIds.isEmpty()) {
            "Все"
        } else {
            filter.selectedCategoryIds
                .mapNotNull { id -> allCategories.find { it.id == id }?.name }
                .joinToString(", ")
        }

    private fun customRangeLabel(filter: AnalyticsFilter): String {
        val start = filter.customStartDate
        val end = filter.customEndDate
        return if (start != null && end != null) {
            "${start.format(dateFormatter)} — ${end.format(dateFormatter)}"
        } else {
            "Выберите даты"
        }
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
