package com.example.fianancetracker.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.fianancetracker.core.ui.components.FinanceEmptyState
import com.example.fianancetracker.core.ui.components.FinanceErrorState
import com.example.fianancetracker.core.ui.components.FinanceFloatingActionButton
import com.example.fianancetracker.core.ui.components.FinanceListContent
import com.example.fianancetracker.core.ui.components.FinanceLoadingState
import com.example.fianancetracker.core.ui.components.FinanceTopBar
import com.example.fianancetracker.core.ui.components.FinanceTotalCard
import com.example.fianancetracker.feature.main.add.account.AddAccountSheetHost
import com.example.fianancetracker.feature.main.add.transaction.AddPicker
import com.example.fianancetracker.feature.main.add.transaction.AddTransactionSheetHost
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val addState by viewModel.addState.collectAsState()
    val addAccountState by viewModel.addAccountState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FinanceTopBar(
                dateText = state.dateLabel,
                onCalendarClick = {},
                onAction1Click = onNavigateToAnalytics
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        icon = { Icon(imageVector = tab.icon(), contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        },
        floatingActionButton = {
            FinanceFloatingActionButton({
                if (state.selectedTab == MainTab.ACCOUNTS) viewModel.openAddAccountSheet()
                else viewModel.openAddSheet()
            })
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (val content = state.current) {
            TabContent.NotLoaded, TabContent.Loading -> FinanceLoadingState(modifier = contentModifier)
            is TabContent.Error -> FinanceErrorState(
                message = content.message,
                onRetry = { viewModel.reloadCurrent() },
                modifier = contentModifier
            )
            is TabContent.Empty -> FinanceEmptyState(message = content.message, modifier = contentModifier)
            is TabContent.Content -> Column(modifier = contentModifier.fillMaxSize()) {
                FinanceTotalCard(label = content.totalLabel, amount = content.totalAmount)
                val isTransactionTab = state.selectedTab != MainTab.ACCOUNTS
                FinanceListContent(
                    items = content.items,
                    modifier = Modifier.fillMaxSize().weight(1f),
                    onItemClick = { id ->
                        if (isTransactionTab) viewModel.openEditSheet(id)
                        else viewModel.openEditAccountSheet(id)
                    }
                )
            }
        }
    }

    if (addState.visible) {
        AddTransactionSheetHost(
            state = addState,
            onDismiss = viewModel::closeAddSheet,
            onKeyClick = viewModel::onAddKey,
            onBackspace = viewModel::onAddBackspace,
            onConfirm = viewModel::confirmAdd,
            onCommentChange = viewModel::onAddComment,
            onCategoryClick = { viewModel.openAddPicker(AddPicker.CATEGORY) },
            onDateClick = { viewModel.openAddPicker(AddPicker.DATE) },
            onTimeClick = { viewModel.openAddPicker(AddPicker.TIME) },
            onAccountClick = { viewModel.openAddPicker(AddPicker.ACCOUNT) },
            onPickCategory = viewModel::selectCategory,
            onPickAccount = viewModel::selectAccount,
            onPickDate = viewModel::selectDate,
            onPickTime = viewModel::selectTime,
            onPickerDismiss = viewModel::dismissAddPicker,
        )
    }

    if (addAccountState.visible) {
        AddAccountSheetHost(
            state = addAccountState,
            onDismiss = viewModel::closeAddAccountSheet,
            onKeyClick = viewModel::onAccountKey,
            onBackspace = viewModel::onAccountBackspace,
            onConfirm = viewModel::confirmAddAccount,
            onNameChange = viewModel::onAccountName,
            onEmojiSelect = viewModel::selectAccountEmoji,
            onCurrencyClick = viewModel::openAccountCurrencyPicker,
            onPickCurrency = viewModel::selectAccountCurrency,
            onCurrencyPickerDismiss = viewModel::dismissAccountCurrencyPicker,
        )
    }
}
