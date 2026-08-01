package com.example.fianancetracker.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.core.ui.components.FinanceEmptyState
import com.example.fianancetracker.core.ui.components.FinanceErrorState
import com.example.fianancetracker.core.ui.components.FinanceFloatingActionButton
import com.example.fianancetracker.core.ui.components.FinanceListContent
import com.example.fianancetracker.core.ui.components.FinanceLoadingState
import com.example.fianancetracker.core.ui.components.FinanceTopBar
import com.example.fianancetracker.core.ui.components.FinanceTotalCard
import com.example.fianancetracker.core.ui.utils.rememberIsOnline
import com.example.fianancetracker.feature.main.add.account.AddAccountSheetHost
import com.example.fianancetracker.feature.main.add.account.AddAccountViewModel
import com.example.fianancetracker.feature.main.add.transaction.AddPicker
import com.example.fianancetracker.feature.main.add.transaction.AddTransactionSheetHost
import com.example.fianancetracker.feature.main.add.transaction.AddTransactionViewModel
import com.example.fianancetracker.feature.main.add.transaction.DatePickerDialogM3
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    addTransactionViewModel: AddTransactionViewModel = koinViewModel(),
    addAccountViewModel: AddAccountViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val addState by addTransactionViewModel.state.collectAsState()
    val addAccountState by addAccountViewModel.state.collectAsState()
    val isOnline = rememberIsOnline()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FinanceTopBar(
                dateText = state.dateLabel,
                onCalendarClick = { showDatePicker = true },
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
                if (state.selectedTab == MainTab.ACCOUNTS) addAccountViewModel.open()
                else addTransactionViewModel.open(isIncome = state.selectedTab == MainTab.INCOMES)
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isOnline) OfflineBanner()
            when (val content = state.current) {
                TabContent.NotLoaded, TabContent.Loading -> FinanceLoadingState()
                is TabContent.Error -> FinanceErrorState(
                    message = content.message,
                    onRetry = { viewModel.reloadCurrent() },
                )
                is TabContent.Empty -> FinanceEmptyState(message = content.message)
                is TabContent.Content -> Column(modifier = Modifier.fillMaxSize()) {
                    FinanceTotalCard(label = content.totalLabel, amount = content.totalAmount)
                    val isTransactionTab = state.selectedTab != MainTab.ACCOUNTS
                    FinanceListContent(
                        items = content.items,
                        modifier = Modifier.fillMaxSize().weight(1f),
                        onItemClick = { id ->
                            if (isTransactionTab) addTransactionViewModel.openEdit(id)
                            else addAccountViewModel.openEdit(id)
                        }
                    )
                }
            }
        }
    }

    if (addState.visible) {
        AddTransactionSheetHost(
            state = addState,
            onDismiss = addTransactionViewModel::close,
            onKeyClick = addTransactionViewModel::onKey,
            onBackspace = addTransactionViewModel::onBackspace,
            onConfirm = addTransactionViewModel::confirm,
            onCommentChange = addTransactionViewModel::onComment,
            onCategoryClick = { addTransactionViewModel.openPicker(AddPicker.CATEGORY) },
            onDateClick = { addTransactionViewModel.openPicker(AddPicker.DATE) },
            onTimeClick = { addTransactionViewModel.openPicker(AddPicker.TIME) },
            onAccountClick = { addTransactionViewModel.openPicker(AddPicker.ACCOUNT) },
            onPickCategory = addTransactionViewModel::selectCategory,
            onPickAccount = addTransactionViewModel::selectAccount,
            onPickDate = addTransactionViewModel::selectDate,
            onPickTime = addTransactionViewModel::selectTime,
            onPickerDismiss = addTransactionViewModel::dismissPicker,
        )
    }

    if (addAccountState.visible) {
        AddAccountSheetHost(
            state = addAccountState,
            onDismiss = addAccountViewModel::close,
            onKeyClick = addAccountViewModel::onKey,
            onBackspace = addAccountViewModel::onBackspace,
            onConfirm = addAccountViewModel::confirm,
            onNameChange = addAccountViewModel::onName,
            onEmojiSelect = addAccountViewModel::selectEmoji,
            onCurrencyClick = addAccountViewModel::openCurrencyPicker,
            onPickCurrency = addAccountViewModel::selectCurrency,
            onCurrencyPickerDismiss = addAccountViewModel::dismissCurrencyPicker,
        )
    }

    if (showDatePicker) {
        DatePickerDialogM3(
            initial = state.selectedDate,
            onConfirm = { day ->
                viewModel.selectDay(day)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Text(
        text = "Нет подключения к интернету — показаны сохранённые данные",
        color = MaterialTheme.colorScheme.onErrorContainer,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
