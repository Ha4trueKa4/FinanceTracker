package com.example.fianancetracker.core.domain.analytics

import java.time.LocalDate

data class AnalyticsFilter(
    val type: TransactionType = TransactionType.ALL,
    val period: Period = Period.CUSTOM,
    val customStartDate: LocalDate? = LocalDate.now().withDayOfMonth(1),
    val customEndDate: LocalDate? = LocalDate.now(),
    val selectedCategoryIds: Set<Long> = emptySet(),
    val selectedAccountName: String? = null
) {
    val effectiveStartDate: LocalDate
        get() = when (period) {
            Period.WEEK -> LocalDate.now().minusWeeks(1)
            Period.MONTH -> LocalDate.now().withDayOfMonth(1)
            Period.QUARTER -> LocalDate.now().minusMonths(3)
            Period.YEAR -> LocalDate.now().minusYears(1)
            Period.CUSTOM -> customStartDate ?: LocalDate.now().withDayOfMonth(1)
        }

    val effectiveEndDate: LocalDate
        get() = when (period) {
            Period.CUSTOM -> customEndDate ?: LocalDate.now()
            else -> LocalDate.now()
        }
}

enum class TransactionType { INCOME, EXPENSE, ALL }

enum class Period { WEEK, MONTH, QUARTER, YEAR, CUSTOM }