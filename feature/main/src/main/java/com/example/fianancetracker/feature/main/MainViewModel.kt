package com.example.fianancetracker.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.CategoryRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.core.ui.utils.accountEmoji
import com.example.fianancetracker.core.ui.utils.toThousand
import com.example.fianancetracker.core.ui.utils.toUserMessage
import com.example.fianancetracker.feature.main.add.account.ACCOUNT_EMOJIS
import com.example.fianancetracker.feature.main.add.account.AddAccountUiState
import com.example.fianancetracker.feature.main.add.transaction.AddPicker
import com.example.fianancetracker.feature.main.add.transaction.AddTransactionUiState
import com.example.fianancetracker.feature.main.add.common.appendKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
class MainViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))

    private val _uiState = MutableStateFlow(MainUiState(dateLabel = LocalDate.now().format(dateFormatter)))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _addState = MutableStateFlow(AddTransactionUiState())
    val addState: StateFlow<AddTransactionUiState> = _addState.asStateFlow()

    private val _addAccountState = MutableStateFlow(AddAccountUiState())
    val addAccountState: StateFlow<AddAccountUiState> = _addAccountState.asStateFlow()

    private var loadedAccounts: List<Account> = emptyList()

    // Сигнал сети по табу: завершилась ли хоть одна попытка обновления и с какой ошибкой.
    // Нужен, чтобы отличать «пустой список из БД, сеть ещё грузится» (Loading)
    // от «пусто и сеть ответила» (Empty/Error). Данные в UI всегда идут из Room-Flow.
    private data class TabSignal(val refreshed: Boolean = false, val error: String? = null)

    private val signals: Map<MainTab, MutableStateFlow<TabSignal>> =
        MainTab.entries.associateWith { MutableStateFlow(TabSignal()) }

    private val collecting = mutableSetOf<MainTab>()

    // Выбранный день для экранов расходов/доходов (кнопка-календарь в шапке). Реактивный —
    // при смене дня flatMapLatest ниже перезапрашивает локальную БД без похода в сеть.
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
    // (в т.ч. после фоновой синхронизации SyncWorker) — экран обновляется сам.
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
                    accountRepository.observeAccounts().onEach { loadedAccounts = it }, signal,
                ) { list, s -> accountsContent(list, s) }
                    .collect { setTab(tab, it) }
            }
        }
    }

    // День меняется → пересобираем поток на диапазон [день; следующий день). Загрузка с сети
    // при этом не трогается (тянем всю историю), фильтруется только локальная выборка.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTransactionsForSelectedDay(isIncome: Boolean) =
        selectedDay.flatMapLatest { day ->
            transactionRepository.observeTransactions(
                isIncome = isIncome,
                startDate = day.toString(),
                endDate = day.plusDays(1).toString(),
            )
        }

    // Сетевое обновление таба: пуш отложенных + подтягивание с сервера. Результат кладём в сигнал.
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

    private fun Transaction.toListItem(isPositive: Boolean) = ListItemModel(
        id = id,
        lead = ListItemModel.Lead(emoji = category.emoji),
        content = ListItemModel.Content(title = title, subtitle = "${category.name} • $date"),
        trail = ListItemModel.Trail(amount = toThousand(amount), isPositive = isPositive),
    )

    private fun Account.toListItem() = ListItemModel(
        id = id,
        lead = ListItemModel.Lead(emoji = emoji.ifEmpty { accountEmoji(type) }),
        content = ListItemModel.Content(
            title = name,
            subtitle = cardNumber.ifEmpty { type.displayName },
        ),
        trail = ListItemModel.Trail(amount = toThousand(balance), isPositive = balance >= 0),
    )

    fun openAddSheet() {
        val isIncome = _uiState.value.selectedTab == MainTab.INCOMES
        _addState.update { it.copy(visible = true, editingId = null, isIncome = isIncome) }
        viewModelScope.launch { loadDictionariesIfNeeded() }
    }

    fun openEditSheet(id: String) {
        _addState.update {
            AddTransactionUiState(
                visible = true,
                loading = true,
                editingId = id,
                categories = it.categories,
                accounts = it.accounts,
            )
        }
        viewModelScope.launch {
            loadDictionariesIfNeeded()
            transactionRepository.getTransactionDetails(id)
                .onSuccess { details ->
                    _addState.update {
                        val income = it.categories
                            .firstOrNull { category -> category.id == details.categoryId }
                            ?.isIncome ?: it.isIncome
                        it.copy(
                            loading = false,
                            isIncome = income,
                            selectedCategoryId = details.categoryId,
                            selectedAccountId = details.accountId.toString(),
                            amountDigits = details.amount.toLong().toString(),
                            comment = details.comment.orEmpty(),
                            date = details.date,
                            time = details.time,
                        )
                    }
                }
                .onFailure {
                    closeAddSheet()
                }
        }
    }

    fun closeAddSheet() {
        _addState.update {
            AddTransactionUiState(categories = it.categories, accounts = it.accounts)
        }
    }

    private suspend fun loadDictionariesIfNeeded() {
        if (_addState.value.categories.isNotEmpty() && _addState.value.accounts.isNotEmpty()) return
        _addState.update { it.copy(loading = true) }
        val categories = categoryRepository.getCategories().getOrDefault(emptyList())
        val accounts = accountRepository.getAccounts().getOrDefault(emptyList())
        _addState.update { it.copy(loading = false, categories = categories, accounts = accounts) }
    }

    fun onAddKey(key: String) =
        _addState.update { it.copy(amountDigits = appendKey(it.amountDigits, key)) }

    fun onAddBackspace() =
        _addState.update { it.copy(amountDigits = it.amountDigits.dropLast(1)) }

    fun onAddComment(text: String) = _addState.update { it.copy(comment = text) }

    fun openAddPicker(picker: AddPicker) = _addState.update { it.copy(openPicker = picker) }

    fun dismissAddPicker() = _addState.update { it.copy(openPicker = AddPicker.NONE) }

    fun selectCategory(id: Long) =
        _addState.update { it.copy(selectedCategoryId = id, openPicker = AddPicker.NONE) }

    fun selectAccount(id: String) =
        _addState.update { it.copy(selectedAccountId = id, openPicker = AddPicker.NONE) }

    fun selectDate(date: LocalDate) =
        _addState.update { it.copy(date = date, openPicker = AddPicker.NONE) }

    fun selectTime(time: LocalTime) =
        _addState.update { it.copy(time = time, openPicker = AddPicker.NONE) }

    fun confirmAdd() {
        val state = _addState.value
        if (state.submitting) return
        if (!state.isValid) {
            _addState.update { it.copy(showErrors = true) }
            return
        }

        val draft = TransactionDraft(
            accountId = state.selectedAccountId!!.toLong(),
            categoryId = state.selectedCategoryId!!,
            amount = (state.amountDigits.toLongOrNull() ?: 0L).toDouble(),
            transactionDate = toIsoInstant(state.date, state.time),
            comment = state.comment.trim(),
        )

        _addState.update { it.copy(submitting = true) }
        viewModelScope.launch {
            val result = state.editingId?.let { id ->
                transactionRepository.updateTransaction(id, draft)
            } ?: transactionRepository.createTransaction(draft)

            result
                .onSuccess {
                    // Локальная запись уже в Room → Flow таба переэмитит сам, ручной reload не нужен.
                    closeAddSheet()
                }
                .onFailure {
                    _addState.update { it.copy(submitting = false) }
                }
        }
    }

    fun openAddAccountSheet() = _addAccountState.update { AddAccountUiState(visible = true) }

    fun openEditAccountSheet(id: String) {
        val account = loadedAccounts.firstOrNull { it.id == id } ?: return
        _addAccountState.update {
            AddAccountUiState(
                visible = true,
                editingId = id,
                name = account.name,
                amountDigits = account.balance.toLong().toString().filter { c -> c.isDigit() },
                emoji = account.emoji.ifEmpty { ACCOUNT_EMOJIS.first() },
                currency = account.currency.ifEmpty { "RUB" },
            )
        }
    }

    fun closeAddAccountSheet() = _addAccountState.update { AddAccountUiState() }

    fun onAccountKey(key: String) =
        _addAccountState.update { it.copy(amountDigits = appendKey(it.amountDigits, key)) }

    fun onAccountBackspace() =
        _addAccountState.update { it.copy(amountDigits = it.amountDigits.dropLast(1)) }

    fun onAccountName(text: String) = _addAccountState.update { it.copy(name = text) }

    fun selectAccountEmoji(emoji: String) = _addAccountState.update { it.copy(emoji = emoji) }

    fun openAccountCurrencyPicker() = _addAccountState.update { it.copy(showCurrencyPicker = true) }

    fun dismissAccountCurrencyPicker() =
        _addAccountState.update { it.copy(showCurrencyPicker = false) }

    fun selectAccountCurrency(code: String) =
        _addAccountState.update { it.copy(currency = code, showCurrencyPicker = false) }

    fun confirmAddAccount() {
        val state = _addAccountState.value
        if (state.submitting) return
        if (!state.isValid) {
            _addAccountState.update { it.copy(showErrors = true) }
            return
        }

        val name = state.name.trim()
        val emoji = state.emoji
        val balance = (state.amountDigits.toLongOrNull() ?: 0L).toDouble()

        _addAccountState.update { it.copy(submitting = true) }
        viewModelScope.launch {
            val result = state.editingId?.let { id ->
                accountRepository.updateAccount(id, name, emoji, balance, state.currency)
            } ?: accountRepository.createAccount(name, emoji, balance, state.currency)

            result
                .onSuccess {
                    // Счёт записан локально → Flow счетов обновит экран сам.
                    closeAddAccountSheet()
                }
                .onFailure {
                    _addAccountState.update { it.copy(submitting = false) }
                }
        }
    }

    private fun toIsoInstant(date: LocalDate, time: LocalTime): String =
        isoUtcMillisFormatter.format(
            LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant()
        )

    private companion object {
        private val isoUtcMillisFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)
    }
}
