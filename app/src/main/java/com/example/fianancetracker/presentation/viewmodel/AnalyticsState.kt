package com.example.fianancetracker.presentation.viewmodel

import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.presentation.components.common.PieChartSlice
import com.example.fianancetracker.presentation.model.ListItemModel

data class AnalyticsState(
    val filter: AnalyticsFilter = AnalyticsFilter(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val slices: List<PieChartSlice> = emptyList(),
    val items: List<ListItemModel> = emptyList(),
    val totalAmount: Double = 0.0
)