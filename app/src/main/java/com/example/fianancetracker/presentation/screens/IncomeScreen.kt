package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.fianancetracker.presentation.components.FinanceEmptyState
import com.example.fianancetracker.presentation.components.FinanceErrorState
import com.example.fianancetracker.presentation.components.FinanceListContent
import com.example.fianancetracker.presentation.components.FinanceLoadingState
import com.example.fianancetracker.presentation.components.FinanceTotalCard
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import com.example.fianancetracker.presentation.viewmodel.IncomeViewModel
import com.example.fianancetracker.presentation.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { error ->
            if (error != null) {
                val result = snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = "Повторить"
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.loadIncomes()
                }
                viewModel.clearError()
            }
        }
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            FinanceLoadingState(modifier = modifier)
        }
        is UiState.Error -> {
            FinanceErrorState(
                message = state.message,
                onRetry = { viewModel.loadIncomes() },
                modifier = modifier
            )
        }
        is UiState.Empty -> {
            FinanceEmptyState(message = "Нет доходов", modifier = modifier)
        }
        is UiState.Content -> {
            val incomeEmojis = mapOf(
                "Продажа старой мебели" to "🛋",
                "Возврат налога" to "📋",
                "Премия за проект" to "💼",
                "Подработка фриланс" to "💻",
                "Сдача квартиры" to "🏠",
                "Кэшбэк на карту" to "💳",
                "Подарок от родителей" to "🎁",
            )
            val items = state.data.map { income ->
                ListItemModel(
                    id = income.id,
                    lead = ListItemModel.Lead(
                        emoji = incomeEmojis[income.title] ?: "💰"
                    ),
                    content = ListItemModel.Content(
                        title = income.title,
                        subtitle = "${income.category.name} • ${income.date}"
                    ),
                    trail = ListItemModel.Trail(
                        amount = toThousand(income.amount),
                        isPositive = true
                    )
                )
            }
            val totalText = toThousand(state.data.sumOf { it.amount })
            Column(modifier = modifier.fillMaxSize()) {
                FinanceTotalCard(
                    label = "доходы всего",
                    amount = totalText
                )
                FinanceListContent(
                    items = items,
                    modifier = Modifier.fillMaxSize().weight(1f)
                )
            }
        }
    }
}