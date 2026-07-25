package com.example.fianancetracker.domain.repository

import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.analytics.AnalyticsData
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income



interface AnalyticsRepository {
    suspend fun getAnalyticsData(): Result<AnalyticsData>
}