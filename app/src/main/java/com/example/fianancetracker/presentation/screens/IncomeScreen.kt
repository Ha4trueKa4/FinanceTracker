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
import com.example.fianancetracker.presentation.viewmodel.IncomeViewModel
import com.example.fianancetracker.presentation.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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
            val items = state.data.map { income ->
                ListItemModel(
                    id = income.id,
                    lead = ListItemModel.Lead(
                        emoji = income.category.emoji
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