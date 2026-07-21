package com.example.fianancetracker.presentation.viewmodel

import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income

data class AnalyticsState(
    val filter: AnalyticsFilter = AnalyticsFilter(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val filteredIncomes: List<Income> = emptyList(),
    val allExpenses: List<Expense> = emptyList(),
    val allIncomes: List<Income> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
