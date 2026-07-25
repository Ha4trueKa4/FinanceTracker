package com.example.fianancetracker.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.core.ui.utils.accountEmoji
import com.example.fianancetracker.core.ui.utils.toThousand
import com.example.fianancetracker.core.ui.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Одна VM на весь главный экран. Табы переключают состояние списка (без навигации),
 * данные каждого таба грузятся лениво — только при первом открытии — и кешируются.
 * Вся трансформация (маппинг в UI-модель, суммы, форматирование) — здесь; экран лишь рендерит.
 */
class MainViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))

    private val _uiState = MutableStateFlow(MainUiState(dateLabel = LocalDate.now().format(dateFormatter)))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        selectTab(MainTab.EXPENSES)
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (_uiState.value.tabs.getValue(tab) is TabContent.NotLoaded) {
            loadTab(tab)
        }
    }

    /** Принудительная перезагрузка текущего таба (кнопка «повторить»). */
    fun reloadCurrent() = loadTab(_uiState.value.selectedTab)

    private fun setTab(tab: MainTab, content: TabContent) {
        _uiState.update { it.copy(tabs = it.tabs + (tab to content)) }
    }

    private fun loadTab(tab: MainTab) {
        setTab(tab, TabContent.Loading)
        viewModelScope.launch {
            when (tab) {
                MainTab.EXPENSES -> loadTransactions(tab, isIncome = false, totalLabel = "расходы всего")
                MainTab.INCOMES -> loadTransactions(tab, isIncome = true, totalLabel = "доходы всего")
                MainTab.ACCOUNTS -> loadAccounts()
            }
        }
    }

    private suspend fun loadTransactions(tab: MainTab, isIncome: Boolean, totalLabel: String) {
        transactionRepository.getTransactions(isIncome)
            .onSuccess { transactions ->
                setTab(tab, if (transactions.isEmpty()) {
                    TabContent.Empty(if (isIncome) "Нет доходов" else "Нет расходов")
                } else {
                    TabContent.Content(
                        totalLabel = totalLabel,
                        totalAmount = toThousand(transactions.sumOf { it.amount }),
                        items = transactions.map { it.toListItem(isPositive = isIncome) },
                    )
                })
            }
            .onFailure { setTab(tab, TabContent.Error(it.toUserMessage())) }
    }

    private suspend fun loadAccounts() {
        accountRepository.getAccounts()
            .onSuccess { accounts ->
                setTab(MainTab.ACCOUNTS, if (accounts.isEmpty()) {
                    TabContent.Empty("Нет счетов")
                } else {
                    TabContent.Content(
                        totalLabel = "баланс всего",
                        totalAmount = toThousand(accounts.sumOf { it.balance }),
                        items = accounts.map { it.toListItem() },
                    )
                })
            }
            .onFailure { setTab(MainTab.ACCOUNTS, TabContent.Error(it.toUserMessage())) }
    }

    private fun Transaction.toListItem(isPositive: Boolean) = ListItemModel(
        id = id,
        lead = ListItemModel.Lead(emoji = category.emoji),
        content = ListItemModel.Content(title = title, subtitle = "${category.name} • $date"),
        trail = ListItemModel.Trail(amount = toThousand(amount), isPositive = isPositive),
    )

    private fun Account.toListItem() = ListItemModel(
        id = id,
        lead = ListItemModel.Lead(emoji = accountEmoji(type)),
        content = ListItemModel.Content(
            title = name,
            subtitle = cardNumber.ifEmpty { type.displayName },
        ),
        trail = ListItemModel.Trail(amount = toThousand(balance), isPositive = balance >= 0),
    )

    /**
     * Добавление операции: оптимистично дописываем в список текущего таба.
     * TODO: POST в сеть (нужен create-эндпоинт в FinanceApi) + инвалидация кеша таба «Счета».
     */
    fun addTransaction() {
        // заготовка под будущий флоу добавления
    }
}
