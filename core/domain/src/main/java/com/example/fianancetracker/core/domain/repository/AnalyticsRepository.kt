package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.analytics.AnalyticsData
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income

interface AnalyticsRepository {
    suspend fun getAnalyticsData(): Result<AnalyticsData>
}
