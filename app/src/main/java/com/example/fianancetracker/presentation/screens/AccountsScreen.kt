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
import kotlinx.coroutines.flow.collectLatest
import com.example.fianancetracker.domain.account.AccountType
import com.example.fianancetracker.presentation.components.FinanceEmptyState
import com.example.fianancetracker.presentation.components.FinanceErrorState
import com.example.fianancetracker.presentation.components.FinanceListContent
import com.example.fianancetracker.presentation.components.FinanceLoadingState
import com.example.fianancetracker.presentation.components.FinanceTotalCard
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import com.example.fianancetracker.presentation.viewmodel.AccountsViewModel
import com.example.fianancetracker.presentation.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collectLatest { error ->
            if (error != null) {
                snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = "Повторить"
                )
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
                onRetry = { viewModel.loadAccounts() },
                modifier = modifier
            )
        }
        is UiState.Empty -> {
            FinanceEmptyState(message = "Нет счетов", modifier = modifier)
        }
        is UiState.Content -> {
            val accountEmojis = mapOf(
                AccountType.DEBIT to "🏦",
                AccountType.SAVINGS to "🐖",
                AccountType.CREDIT to "💳",
                AccountType.CASH to "💵",
            )
            val items = state.data.map { account ->
                ListItemModel(
                    id = account.id,
                    lead = ListItemModel.Lead(
                        emoji = accountEmojis[account.type] ?: "💰"
                    ),
                    content = ListItemModel.Content(
                        title = account.name,
                        subtitle = account.cardNumber.ifEmpty { account.type.displayName }
                    ),
                    trail = ListItemModel.Trail(
                        amount = toThousand(account.balance),
                        isPositive = account.balance >= 0
                    )
                )
            }
            val totalText = toThousand(state.data.sumOf { it.balance })
            Column(modifier = modifier.fillMaxSize()) {
                FinanceTotalCard(
                    label = "баланс всего",
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