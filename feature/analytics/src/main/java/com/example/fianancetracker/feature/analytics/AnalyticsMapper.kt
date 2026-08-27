package com.example.fianancetracker.feature.analytics

import androidx.compose.ui.graphics.Color
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.analytics.Period
import com.example.fianancetracker.core.domain.analytics.TransactionType
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.core.ui.utils.toThousand
import com.example.fianancetracker.feature.analytics.components.PieChartSlice
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Чистая логика экрана аналитики: фильтрация транзакций и сборка [AnalyticsState] под фильтр. */
class AnalyticsMapper {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun map(
        expenses: List<Expense>,
        incomes: List<Income>,
        accounts: List<Account>,
        categories: List<Category>,
        filter: AnalyticsFilter,
    ): AnalyticsState {
        val items = combinedItems(expenses, incomes, filter)
        val totalAmount = items.sumOf { it.amount }

        return AnalyticsState(
            accounts = accounts,
            categories = categories,
            slices = slices(items, totalAmount),
            items = items.map { it.toListItem() },
            totalFormatted = toThousand(totalAmount),
            typeBadge = typeBadge(filter.type),
            periodBadge = periodBadge(filter),
            categoriesBadge = categoriesBadge(filter, categories),
            accountBadge = filter.selectedAccountName ?: "Все счета",
            customRangeLabel = customRangeLabel(filter),
            filter = filter,
        )
    }

    private fun combinedItems(
        expenses: List<Expense>,
        incomes: List<Income>,
        filter: AnalyticsFilter,
    ): List<Transaction> {
        val filteredExpenses = expenses.filter { matchesFilter(it, filter) }
        val filteredIncomes = incomes.filter { matchesFilter(it, filter) }
        return when (filter.type) {
            TransactionType.EXPENSE -> filteredExpenses
            TransactionType.INCOME -> filteredIncomes
            TransactionType.ALL -> filteredExpenses + filteredIncomes
        }.sortedByDescending { parseDate(it.date) ?: LocalDate.MIN }
    }

    private fun slices(items: List<Transaction>, totalAmount: Double): List<PieChartSlice> =
        items.groupBy { it.category }
            .mapValues { (_, group) -> group.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
            .filter { it.value > 0 }
            .map { (category, amount) ->
                val percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
                PieChartSlice(
                    label = category.name,
                    color = sliceColor(category.name),
                    percentage = percentage,
                    amountText = toThousand(amount),
                    percentText = "${"%.0f".format(percentage)}%",
                )
            }

    private fun Transaction.toListItem() = ListItemModel(
        id = id,
        lead = ListItemModel.Lead(emoji = category.emoji),
        content = ListItemModel.Content(title = title, subtitle = accountName),
        trail = ListItemModel.Trail(amount = toThousand(amount), isPositive = amount >= 0),
    )

    private fun sliceColor(label: String): Color {
        val hue = ((label.hashCode() and Int.MAX_VALUE) % 360).toFloat()
        return Color.hsl(hue, 0.6f, 0.55f)
    }

    private fun typeBadge(type: TransactionType): String = when (type) {
        TransactionType.EXPENSE -> "Расходы"
        TransactionType.INCOME -> "Доходы"
        TransactionType.ALL -> "Всё"
    }

    private fun periodBadge(filter: AnalyticsFilter): String = when (filter.period) {
        Period.WEEK -> "За неделю"
        Period.MONTH -> "За месяц"
        Period.QUARTER -> "За квартал"
        Period.YEAR -> "За год"
        Period.CUSTOM -> customRange(filter, fallback = "Произвольный", separator = "–")
    }

    private fun categoriesBadge(filter: AnalyticsFilter, categories: List<Category>): String =
        if (filter.selectedCategoryIds.isEmpty()) {
            "Все"
        } else {
            filter.selectedCategoryIds
                .mapNotNull { id -> categories.find { it.id == id }?.name }
                .joinToString(", ")
        }

    private fun customRangeLabel(filter: AnalyticsFilter): String =
        customRange(filter, fallback = "Выберите даты", separator = "—")

    private fun customRange(filter: AnalyticsFilter, fallback: String, separator: String): String {
        val start = filter.customStartDate
        val end = filter.customEndDate
        return if (start != null && end != null) {
            "${start.format(dateFormatter)} $separator ${end.format(dateFormatter)}"
        } else {
            fallback
        }
    }

    private fun matchesFilter(transaction: Transaction, filter: AnalyticsFilter): Boolean {
        val categoryIds = filter.selectedCategoryIds
        if (categoryIds.isNotEmpty() && transaction.category.id !in categoryIds) return false
        if (filter.selectedAccountName != null && transaction.accountName != filter.selectedAccountName) {
            return false
        }
        return isDateInRange(transaction.date, filter.effectiveStartDate, filter.effectiveEndDate)
    }

    private fun isDateInRange(dateStr: String, start: LocalDate, end: LocalDate): Boolean = try {
        val date = LocalDate.parse(dateStr, dateFormatter)
        !date.isBefore(start) && !date.isAfter(end)
    } catch (_: Exception) {
        true
    }

    private fun parseDate(dateStr: String): LocalDate? = try {
        LocalDate.parse(dateStr, dateFormatter)
    } catch (_: Exception) {
        null
    }
}
