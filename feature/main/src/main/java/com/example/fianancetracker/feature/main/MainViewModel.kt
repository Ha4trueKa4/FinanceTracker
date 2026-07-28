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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        selectTab(MainTab.EXPENSES)
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (_uiState.value.tabs.getValue(tab) is TabContent.NotLoaded) {
            loadTab(tab)
        }
    }

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
                loadedAccounts = accounts
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
                    closeAddSheet()
                    refreshAllTabs()
                }
                .onFailure {
                    _addState.update { it.copy(submitting = false) }
                }
        }
    }

    private fun refreshAllTabs() {
        _uiState.update { it.copy(tabs = MainTab.entries.associateWith { TabContent.NotLoaded }) }
        loadTab(_uiState.value.selectedTab)
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
                    closeAddAccountSheet()
                    loadTab(MainTab.ACCOUNTS)
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
