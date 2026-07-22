package com.example.fianancetracker.domain.analytics

import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income

data class AnalyticsData(
    val accounts: List<Account>,
    val expenses: List<Expense>,
    val incomes: List<Income>,
    val categories: List<Category>
)
