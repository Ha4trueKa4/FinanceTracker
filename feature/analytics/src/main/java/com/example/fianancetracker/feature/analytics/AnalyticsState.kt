package com.example.fianancetracker.feature.analytics

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.feature.analytics.components.PieChartSlice

/**
 * Готовое к рендеру состояние аналитики. Все подписи (бэйджи фильтров, суммы) уже
 * посчитаны во ViewModel — экран только отображает. `filter`/`accounts`/`categories`
 * нужны боттом-шитам как исходные значения для выбора.
 */
data class AnalyticsState(
    val filter: AnalyticsFilter = AnalyticsFilter(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val slices: List<PieChartSlice> = emptyList(),
    val items: List<ListItemModel> = emptyList(),
    val totalFormatted: String = "",
    val typeBadge: String = "",
    val periodBadge: String = "",
    val categoriesBadge: String = "",
    val accountBadge: String = "",
    val customRangeLabel: String = "",
)
