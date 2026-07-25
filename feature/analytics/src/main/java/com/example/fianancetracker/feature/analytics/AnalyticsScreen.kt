package com.example.fianancetracker.feature.analytics

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
import com.example.fianancetracker.feature.analytics.R
import com.example.fianancetracker.core.domain.analytics.Period
import com.example.fianancetracker.core.domain.analytics.TransactionType
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.AccountBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.CategoryBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.CustomDateRangeSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.DetailBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.FilterBottomSheet
import com.example.fianancetracker.feature.analytics.components.bottom_sheets.PeriodBottomSheet
import com.example.fianancetracker.core.ui.components.FinanceEmptyState
import com.example.fianancetracker.core.ui.components.FinanceErrorState
import com.example.fianancetracker.core.ui.components.FinanceListItem
import com.example.fianancetracker.core.ui.components.FinanceLoadingState
import com.example.fianancetracker.feature.analytics.components.FinancePieChart
import com.example.fianancetracker.feature.analytics.components.filters.FilterRow
import com.example.fianancetracker.feature.analytics.AnalyticsUiState
import com.example.fianancetracker.feature.analytics.AnalyticsViewModel

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
        when (val s = state) {
            is AnalyticsUiState.Loading -> {
                FinanceLoadingState(modifier = Modifier.padding(padding))
            }

            is AnalyticsUiState.Error -> {
                FinanceErrorState(
                    message = s.message,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.padding(padding)
                )
            }

            is AnalyticsUiState.Empty -> {
                FinanceEmptyState(
                    message = "Нет операций за выбранный период",
                    modifier = Modifier.padding(padding)
                )
            }

            is AnalyticsUiState.Content -> {
                val data = s.data

                if (data.items.isEmpty()) {
                    FinanceEmptyState(
                        message = "Нет операций за выбранный период",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            FinancePieChart(
                                slices = data.slices,
                                totalAmount = data.totalFormatted,
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
                                badgeText = data.typeBadge,
                                onClick = { showTypeSheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_period),
                                label = "Период",
                                badgeText = data.periodBadge,
                                onClick = { showPeriodSheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_category),
                                label = "Статьи",
                                badgeText = data.categoriesBadge,
                                onClick = { showCategorySheet = true }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            FilterRow(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_filter_account),
                                label = "Счёт",
                                badgeText = data.accountBadge,
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

                        items(data.items, key = { it.id }) { item ->
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
                            slices = data.slices,
                            totalFormatted = data.totalFormatted,
                            onDismiss = { showDetailSheet = false }
                        )
                    }

                    if (showCategorySheet) {
                        CategoryBottomSheet(
                            categories = data.categories,
                            selectedIds = data.filter.selectedCategoryIds,
                            onDone = { ids ->
                                viewModel.updateFilter(data.filter.copy(selectedCategoryIds = ids))
                                showCategorySheet = false
                            },
                            onDismiss = { showCategorySheet = false }
                        )
                    }
                }

                if (showTypeSheet) {
                    FilterBottomSheet(
                        title = "Тип",
                        options = listOf("Расходы", "Доходы", "Всё"),
                        selectedOption = data.typeBadge,
                        onSelect = { option ->
                            val newType = when (option) {
                                "Расходы" -> TransactionType.EXPENSE
                                "Доходы" -> TransactionType.INCOME
                                else -> TransactionType.ALL
                            }
                            viewModel.updateFilter(data.filter.copy(type = newType))
                            showTypeSheet = false
                        },
                        onDismiss = { showTypeSheet = false }
                    )
                }

                if (showPeriodSheet) {
                    PeriodBottomSheet(
                        filter = data.filter,
                        customRangeLabel = data.customRangeLabel,
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
                        initialStartDate = data.filter.customStartDate,
                        initialEndDate = data.filter.customEndDate,
                        onApply = { startDate, endDate ->
                            viewModel.updateFilter(
                                data.filter.copy(
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
                        accounts = data.accounts,
                        selectedAccountName = data.filter.selectedAccountName,
                        onSelect = { accountName ->
                            viewModel.updateFilter(data.filter.copy(selectedAccountName = accountName))
                            showAccountSheet = false
                        },
                        onDismiss = { showAccountSheet = false }
                    )
                }
            }
        }
    }
}