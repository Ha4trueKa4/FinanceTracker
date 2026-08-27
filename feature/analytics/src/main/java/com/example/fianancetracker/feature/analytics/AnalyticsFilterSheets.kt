package com.example.fianancetracker.feature.analytics

import androidx.compose.runtime.Composable
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.analytics.Period
import com.example.fianancetracker.core.domain.analytics.TransactionType
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.AccountBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.CategoryBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.CustomDateRangeSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.DetailBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.FilterBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.PeriodBottomSheet

/** Хост bottom-sheet'ов фильтров аналитики: показывает нужный лист по флагам [sheets]. */
@Composable
internal fun AnalyticsFilterSheets(
    data: AnalyticsState,
    sheets: AnalyticsSheets,
    onFilterUpdate: (AnalyticsFilter) -> Unit,
) {
    if (sheets.detail) {
        DetailBottomSheet(
            slices = data.slices,
            totalFormatted = data.totalFormatted,
            onDismiss = { sheets.detail = false },
        )
    }

    if (sheets.category) {
        CategoryBottomSheet(
            categories = data.categories,
            selectedIds = data.filter.selectedCategoryIds,
            onDone = { ids ->
                onFilterUpdate(data.filter.copy(selectedCategoryIds = ids))
                sheets.category = false
            },
            onDismiss = { sheets.category = false },
        )
    }

    if (sheets.type) {
        FilterBottomSheet(
            title = "Тип",
            options = listOf("Расходы", "Доходы", "Всё"),
            selectedOption = data.typeBadge,
            onSelect = { option ->
                onFilterUpdate(data.filter.copy(type = option.toTransactionType()))
                sheets.type = false
            },
            onDismiss = { sheets.type = false },
        )
    }

    if (sheets.period) {
        PeriodBottomSheet(
            filter = data.filter,
            customRangeLabel = data.customRangeLabel,
            onApply = { newFilter ->
                onFilterUpdate(newFilter)
                sheets.period = false
            },
            onCustomSelected = {
                sheets.period = false
                sheets.customDate = true
            },
            onDismiss = { sheets.period = false },
        )
    }

    if (sheets.customDate) {
        CustomDateRangeSheet(
            initialStartDate = data.filter.customStartDate,
            initialEndDate = data.filter.customEndDate,
            onApply = { startDate, endDate ->
                onFilterUpdate(
                    data.filter.copy(
                        period = Period.CUSTOM,
                        customStartDate = startDate,
                        customEndDate = endDate,
                    )
                )
                sheets.customDate = false
            },
            onDismiss = { sheets.customDate = false },
        )
    }

    if (sheets.account) {
        AccountBottomSheet(
            accounts = data.accounts,
            selectedAccountName = data.filter.selectedAccountName,
            onSelect = { accountName ->
                onFilterUpdate(data.filter.copy(selectedAccountName = accountName))
                sheets.account = false
            },
            onDismiss = { sheets.account = false },
        )
    }
}

private fun String.toTransactionType(): TransactionType = when (this) {
    "Расходы" -> TransactionType.EXPENSE
    "Доходы" -> TransactionType.INCOME
    else -> TransactionType.ALL
}
