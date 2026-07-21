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
import com.example.fianancetracker.presentation.viewmodel.ExpensesViewModel
import com.example.fianancetracker.presentation.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,
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
                    viewModel.loadExpenses()
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
                onRetry = { viewModel.loadExpenses() },
                modifier = modifier
            )
        }
        is UiState.Empty -> {
            FinanceEmptyState(message = "Нет расходов", modifier = modifier)
        }
        is UiState.Content -> {
            val expenseEmojis = mapOf(
                "Кофе в старбакс" to "☕",
                "Обед в кафе" to "🍕",
                "Такси до работы" to "🚕",
                "Продукты в магазине" to "🛒",
                "Кино с друзьями" to "🎬",
                "Аптека" to "💊",
                "Коммунальные платежи" to "💡",
                "Курсы программирования" to "💻",
                "Новая футболка" to "👕",
                "Ужин в ресторане" to "🍽️",
                "Метро" to "🚇",
                "Подписка Spotify" to "🎵",
            )
            val items = state.data.map { expense ->
                ListItemModel(
                    id = expense.id,
                    lead = ListItemModel.Lead(
                        emoji = expenseEmojis[expense.title] ?: "💸"
                    ),
                    content = ListItemModel.Content(
                        title = expense.title,
                        subtitle = "${expense.category.name} • ${expense.date}"
                    ),
                    trail = ListItemModel.Trail(
                        amount = toThousand(expense.amount),
                        isPositive = false
                    )
                )
            }
            val totalText = toThousand(state.data.sumOf { it.amount })
            Column(modifier = modifier.fillMaxSize()) {
                FinanceTotalCard(
                    label = "расходы всего",
                    amount = totalText,
                )
                FinanceListContent(
                    items = items,
                    modifier = Modifier.fillMaxSize().weight(1f)
                )
            }
        }
    }
}