package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.R
import com.example.fianancetracker.domain.analytics.Period
import com.example.fianancetracker.domain.analytics.TransactionType
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.presentation.components.bottom_sheets.AccountBottomSheet
import com.example.fianancetracker.presentation.components.bottom_sheets.CategoryBottomSheet
import com.example.fianancetracker.presentation.components.bottom_sheets.CustomDateRangeSheet
import com.example.fianancetracker.presentation.components.bottom_sheets.DetailBottomSheet
import com.example.fianancetracker.presentation.components.bottom_sheets.FilterBottomSheet
import com.example.fianancetracker.presentation.components.bottom_sheets.PeriodBottomSheet
import com.example.fianancetracker.presentation.components.filters.FilterRow
import com.example.fianancetracker.presentation.components.common.FinanceEmptyState
import com.example.fianancetracker.presentation.components.common.FinanceErrorState
import com.example.fianancetracker.presentation.components.common.FinanceListItem
import com.example.fianancetracker.presentation.components.common.FinanceLoadingState
import com.example.fianancetracker.presentation.components.common.FinancePieChart
import com.example.fianancetracker.presentation.components.common.PieChartSlice
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import com.example.fianancetracker.presentation.viewmodel.AnalyticsViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    var showTypeSheet by remember { mutableStateOf(false) }
    var showPeriodSheet by remember { mutableStateOf(false) }
    var showCustomDateSheet by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Аналитика",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1D1B20)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { padding ->
        when {
            state.isLoading -> {
                FinanceLoadingState(modifier = Modifier.padding(padding))
            }

            state.error != null -> {
                FinanceErrorState(
                    message = state.error ?: "",
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                val allTransactions = state.filteredExpenses + state.filteredIncomes
                val combinedItems = when (state.filter.type) {
                    TransactionType.EXPENSE -> state.filteredExpenses
                    TransactionType.INCOME -> state.filteredIncomes
                    TransactionType.ALL -> allTransactions
                }.sortedByDescending { item ->
                    when (item) {
                        is Expense -> item.date
                        is Income -> item.date
                        else -> ""
                    }
                }

                if (combinedItems.isEmpty()) {
                    FinanceEmptyState(
                        message = "Нет операций за выбранный период",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    val totalAmount = combinedItems.sumOf {
                        when (it) {
                            is Expense -> it.amount
                            is Income -> it.amount
                            else -> 0.0
                        }
                    }

                    val categoryTotals = combinedItems
                        .groupBy {
                            when (it) {
                                is Expense -> it.category
                                is Income -> it.category
                                else -> return@groupBy null
                            }
                        }
                        .mapNotNull { (category, items) ->
                            category?.let { cat ->
                                cat to items.sumOf { item ->
                                    when (item) {
                                        is Expense -> item.amount
                                        is Income -> item.amount
                                        else -> 0.0
                                    }
                                }
                            }
                        }
                        .sortedByDescending { it.second }

                    val slices = categoryTotals
                        .filter { it.second > 0 }
                        .map { (category, amount) ->
                            PieChartSlice(
                                label = category.name,
                                emoji = category.emoji,
                                amount = amount,
                                color = Color.Unspecified,
                                percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
                            )
                        }

                    val availableCategories = categoryTotals.map { it.first }

                    val items = combinedItems.mapNotNull { transaction ->
                        val (id, title, amount, category, accountName) = when (transaction) {
                            is Expense -> listOf(
                                transaction.id,
                                transaction.title,
                                transaction.amount,
                                transaction.category,
                                transaction.accountName
                            )

                            is Income -> listOf(
                                transaction.id,
                                transaction.title,
                                transaction.amount,
                                transaction.category,
                                transaction.accountName
                            )

                            else -> return@mapNotNull null
                        }
                        ListItemModel(
                            id = id as String,
                            lead = ListItemModel.Lead(emoji = (category as Category).emoji),
                            content = ListItemModel.Content(
                                title = title as String,
                                subtitle = accountName as String
                            ),
                            trail = ListItemModel.Trail(
                                amount = toThousand(amount as Double),
                                isPositive = (amount as Double) >= 0
                            )
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            FinancePieChart(
                                slices = slices,
                                totalAmount = toThousand(totalAmount),
                                totalLabel = "Всего за период",
                                onClick = { showDetailSheet = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        item {
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_type),
                                label = "Тип",
                                badgeText = when (state.filter.type) {
                                    TransactionType.ALL -> "Всё"
                                    TransactionType.EXPENSE -> "Расходы"
                                    TransactionType.INCOME -> "Доходы"
                                },
                                onClick = { showTypeSheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_period),
                                label = "Период",
                                badgeText = when (state.filter.period) {
                                    Period.WEEK -> "За неделю"
                                    Period.MONTH -> "За месяц"
                                    Period.QUARTER -> "За квартал"
                                    Period.YEAR -> "За год"
                                    Period.CUSTOM -> {
                                        val start = state.filter.customStartDate
                                        val end = state.filter.customEndDate
                                        val f = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                        if (start != null && end != null) {
                                            "${start.format(f)} – ${end.format(f)}"
                                        } else {
                                            "Произвольный"
                                        }
                                    }
                                },
                                onClick = { showPeriodSheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_category),
                                label = "Статьи",
                                badgeText = if (state.filter.selectedCategoryIds.isEmpty()) {
                                    "Все"
                                } else {
                                    state.filter.selectedCategoryIds.mapNotNull { id ->
                                        state.categories.find { it.id == id }?.name
                                    }.joinToString(", ")
                                },
                                onClick = { showCategorySheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_account),
                                label = "Счёт",
                                badgeText = state.filter.selectedAccountName
                                    ?: "Все счета",
                                onClick = { showAccountSheet = true }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Транзакции",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        items(items, key = { it.id }) { item ->
                            FinanceListItem(item = item)
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    if (showDetailSheet) {
                        DetailBottomSheet(
                            slices = slices,
                            totalAmount = totalAmount,
                            onDismiss = { showDetailSheet = false }
                        )
                    }

                    if (showCategorySheet) {
                        CategoryBottomSheet(
                            categories = availableCategories,
                            selectedIds = state.filter.selectedCategoryIds,
                            onDone = { ids ->
                                viewModel.updateFilter(state.filter.copy(selectedCategoryIds = ids))
                                showCategorySheet = false
                            },
                            onDismiss = { showCategorySheet = false }
                        )
                    }
                }
            }
        }
    }

    if (showTypeSheet) {
        FilterBottomSheet(
            title = "Тип",
            options = listOf("Расходы", "Доходы", "Всё"),
            selectedOption = when (state.filter.type) {
                TransactionType.EXPENSE -> "Расходы"
                TransactionType.INCOME -> "Доходы"
                TransactionType.ALL -> "Всё"
            },
            onSelect = { option ->
                val newType = when (option) {
                    "Расходы" -> TransactionType.EXPENSE
                    "Доходы" -> TransactionType.INCOME
                    "Всё" -> TransactionType.ALL
                    else -> TransactionType.ALL
                }
                viewModel.updateFilter(state.filter.copy(type = newType))
                showTypeSheet = false
            },
            onDismiss = { showTypeSheet = false }
        )
    }

    if (showPeriodSheet) {
        PeriodBottomSheet(
            filter = state.filter,
            onApply = { newFilter ->
                viewModel.updateFilter(newFilter)
                showPeriodSheet = false
            },
            onCustomSelected = {
                showPeriodSheet = false
                showCustomDateSheet = true
            },
            onDismiss = { showPeriodSheet = false }
        )
    }

    if (showCustomDateSheet) {
        CustomDateRangeSheet(
            initialStartDate = state.filter.customStartDate,
            initialEndDate = state.filter.customEndDate,
            onApply = { startDate, endDate ->
                viewModel.updateFilter(
                    state.filter.copy(
                        period = Period.CUSTOM,
                        customStartDate = startDate,
                        customEndDate = endDate
                    )
                )
                showCustomDateSheet = false
            },
            onDismiss = { showCustomDateSheet = false }
        )
    }

    if (showAccountSheet) {
        AccountBottomSheet(
            accounts = state.accounts,
            selectedAccountName = state.filter.selectedAccountName,
            onSelect = { accountName ->
                viewModel.updateFilter(state.filter.copy(selectedAccountName = accountName))
                showAccountSheet = false
            },
            onDismiss = { showAccountSheet = false }
        )
    }
}