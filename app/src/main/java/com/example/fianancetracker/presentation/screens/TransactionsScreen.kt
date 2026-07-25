package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.fianancetracker.presentation.components.common.FinanceEmptyState
import com.example.fianancetracker.presentation.components.common.FinanceErrorState
import com.example.fianancetracker.presentation.components.common.FinanceListContent
import com.example.fianancetracker.presentation.components.common.FinanceLoadingState
import com.example.fianancetracker.presentation.components.common.FinanceTotalCard
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import com.example.fianancetracker.presentation.viewmodel.TransactionsUiState
import com.example.fianancetracker.presentation.viewmodel.TransactionsViewModel

/**
 * Единый экран для расходов и доходов. Отличия сводятся к [isIncome]:
 * знак суммы, подписи и текст пустого состояния.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is TransactionsUiState.Loading -> {
            FinanceLoadingState(modifier = modifier)
        }
        is TransactionsUiState.Error -> {
            FinanceErrorState(
                message = state.message,
                onRetry = { viewModel.load() },
                modifier = modifier
            )
        }
        is TransactionsUiState.Empty -> {
            FinanceEmptyState(
                message = if (isIncome) "Нет доходов" else "Нет расходов",
                modifier = modifier
            )
        }
        is TransactionsUiState.Content -> {
            val items = state.items.map { transaction ->
                ListItemModel(
                    id = transaction.id,
                    lead = ListItemModel.Lead(
                        emoji = transaction.category.emoji
                    ),
                    content = ListItemModel.Content(
                        title = transaction.title,
                        subtitle = "${transaction.category.name} • ${transaction.date}"
                    ),
                    trail = ListItemModel.Trail(
                        amount = toThousand(transaction.amount),
                        isPositive = isIncome
                    )
                )
            }
            val totalText = toThousand(state.items.sumOf { it.amount })
            Column(modifier = modifier.fillMaxSize()) {
                FinanceTotalCard(
                    label = if (isIncome) "доходы всего" else "расходы всего",
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
