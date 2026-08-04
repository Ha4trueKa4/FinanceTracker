package com.example.fianancetracker.feature.analytics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import com.example.fianancetracker.core.ui.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel экрана аналитики: хранит фильтр и сырые данные, пересобирает состояние через [AnalyticsMapper]. */
class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val context: Context,
    private val mapper: AnalyticsMapper = AnalyticsMapper(),
) : ViewModel() {

    private val _filter = MutableStateFlow(AnalyticsFilter())
    val filter: StateFlow<AnalyticsFilter> = _filter.asStateFlow()

    private val _state = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    private var expenses: List<Expense> = emptyList()
    private var incomes: List<Income> = emptyList()
    private var accounts: List<Account> = emptyList()
    private var categories: List<Category> = emptyList()

    init {
        loadData()
    }

    fun updateFilter(newFilter: AnalyticsFilter) {
        _filter.value = newFilter
        recompute()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = AnalyticsUiState.Loading
            analyticsRepository.getAnalyticsData()
                .onSuccess { data ->
                    expenses = data.expenses
                    incomes = data.incomes
                    accounts = data.accounts
                    categories = data.categories
                    recompute()
                }
                .onFailure { _state.value = AnalyticsUiState.Error(it.toUserMessage(context)) }
        }
    }

    private fun recompute() {
        _state.value = AnalyticsUiState.Content(
            mapper.map(expenses, incomes, accounts, categories, _filter.value)
        )
    }
}
