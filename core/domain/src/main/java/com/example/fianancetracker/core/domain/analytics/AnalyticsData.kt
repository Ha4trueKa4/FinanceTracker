package com.example.fianancetracker.core.domain.analytics

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income

data class AnalyticsData(
    val accounts: List<Account>,
    val expenses: List<Expense>,
    val incomes: List<Income>,
    val categories: List<Category>
)
