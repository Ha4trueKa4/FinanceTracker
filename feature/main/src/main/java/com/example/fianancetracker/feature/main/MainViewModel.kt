package com.example.fianancetracker.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.ui.utils.toThousand
import com.example.fianancetracker.core.ui.utils.toUserMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** ViewModel главного экрана: табы (расходы/доходы/счета), выбранный день и реактивные списки из Room. */
class MainViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))

    private val _uiState =
        MutableStateFlow(MainUiState(dateLabel = LocalDate.now().format(dateFormatter)))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Сигнал сети по табу: завершилась ли попытка обновления и с какой ошибкой. Нужен, чтобы отличать
    // «пусто из БД, сеть ещё грузится» (Loading) от «пусто и сеть ответила» (Empty/Error).
    private data class TabSignal(val refreshed: Boolean = false, val error: String? = null)

    private val signals: Map<MainTab, MutableStateFlow<TabSignal>> =
        MainTab.entries.associateWith { MutableStateFlow(TabSignal()) }

    private val collecting = mutableSetOf<MainTab>()

    // Выбранный день для расходов/доходов (кнопка-календарь в шапке).
    private val selectedDay = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            selectedDay.collect { day ->
                _uiState.update { it.copy(dateLabel = day.format(dateFormatter), selectedDate = day) }
            }
        }
        selectTab(MainTab.EXPENSES)
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        startCollecting(tab)
        refresh(tab)
    }

    fun selectDay(day: LocalDate) {
        selectedDay.value = day
    }

    fun reloadCurrent() = refresh(_uiState.value.selectedTab)

    private fun setTab(tab: MainTab, content: TabContent) {
        _uiState.update { it.copy(tabs = it.tabs + (tab to content)) }
    }

    // Подписка на Room-Flow таба: живёт до onCleared, переэмитит при любом изменении БД
    // (в т.ч. после фоновой синхронизации) — экран обновляется сам.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startCollecting(tab: MainTab) {
        if (!collecting.add(tab)) return
        val signal = signals.getValue(tab)
        viewModelScope.launch {
            when (tab) {
                MainTab.EXPENSES -> combine(
                    observeTransactionsForSelectedDay(isIncome = false), signal,
                ) { list, s -> transactionsContent(list, isIncome = false, "расходы всего", "Нет расходов", s) }
                    .collect { setTab(tab, it) }

                MainTab.INCOMES -> combine(
                    observeTransactionsForSelectedDay(isIncome = true), signal,
                ) { list, s -> transactionsContent(list, isIncome = true, "доходы всего", "Нет доходов", s) }
                    .collect { setTab(tab, it) }

                MainTab.ACCOUNTS -> combine(
                    accountRepository.observeAccounts(), signal,
                ) { list, s -> accountsContent(list, s) }
                    .collect { setTab(tab, it) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTransactionsForSelectedDay(isIncome: Boolean) =
        selectedDay.flatMapLatest { day ->
            transactionRepository.observeTransactions(
                isIncome = isIncome,
                startDate = day.toString(),
                endDate = day.plusDays(1).toString(),
            )
        }

    private fun refresh(tab: MainTab) {
        viewModelScope.launch {
            val result = when (tab) {
                MainTab.EXPENSES, MainTab.INCOMES -> transactionRepository.refresh()
                MainTab.ACCOUNTS -> accountRepository.refresh()
            }
            signals.getValue(tab).update {
                it.copy(refreshed = true, error = result.exceptionOrNull()?.toUserMessage())
            }
        }
    }

    private fun transactionsContent(
        transactions: List<Transaction>,
        isIncome: Boolean,
        totalLabel: String,
        emptyText: String,
        signal: TabSignal,
    ): TabContent = when {
        transactions.isNotEmpty() -> TabContent.Content(
            totalLabel = totalLabel,
            totalAmount = toThousand(transactions.sumOf { it.amount }),
            items = transactions.map { it.toListItem(isPositive = isIncome) },
        )

        signal.error != null -> TabContent.Error(signal.error)
        signal.refreshed -> TabContent.Empty(emptyText)
        else -> TabContent.Loading
    }

    private fun accountsContent(accounts: List<Account>, signal: TabSignal): TabContent = when {
        accounts.isNotEmpty() -> TabContent.Content(
            totalLabel = "баланс всего",
            totalAmount = toThousand(accounts.sumOf { it.balance }),
            items = accounts.map { it.toListItem() },
        )

        signal.error != null -> TabContent.Error(signal.error)
        signal.refreshed -> TabContent.Empty("Нет счетов")
        else -> TabContent.Loading
    }
}
