package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.fianancetracker.domain.account.AccountType
import com.example.fianancetracker.presentation.components.common.FinanceEmptyState
import com.example.fianancetracker.presentation.components.common.FinanceErrorState
import com.example.fianancetracker.presentation.components.common.FinanceListContent
import com.example.fianancetracker.presentation.components.common.FinanceLoadingState
import com.example.fianancetracker.presentation.components.common.FinanceTotalCard
import com.example.fianancetracker.presentation.model.ListItemModel
import com.example.fianancetracker.presentation.utils.toThousand
import com.example.fianancetracker.presentation.viewmodel.AccountsUiState
import com.example.fianancetracker.presentation.viewmodel.AccountsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AccountsUiState.Loading -> {
            FinanceLoadingState(modifier = modifier)
        }
        is AccountsUiState.Error -> {
            FinanceErrorState(
                message = state.message,
                onRetry = { viewModel.loadAccounts() },
                modifier = modifier
            )
        }
        is AccountsUiState.Empty -> {
            FinanceEmptyState(message = "Нет счетов", modifier = modifier)
        }
        is AccountsUiState.Content -> {
            val accountEmojis = mapOf(
                AccountType.DEBIT to "🏦",
                AccountType.SAVINGS to "🐖",
                AccountType.CREDIT to "💳",
                AccountType.CASH to "💵",
            )
            val items = state.accounts.map { account ->
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
            val totalText = toThousand(state.accounts.sumOf { it.balance })
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