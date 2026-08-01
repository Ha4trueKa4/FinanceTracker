package com.example.fianancetracker.feature.main.add.transaction

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.category.Category
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val NOT_SELECTED = "Не выбрано"

private val addDateFormatter =
    DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))
private val addTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

data class AddTransactionUiState(
    val visible: Boolean = false,
    val loading: Boolean = false,
    val editingId: String? = null,
    val submitting: Boolean = false,
    val isIncome: Boolean = false,
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedAccountId: String? = null,
    val amountDigits: String = "",
    val comment: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val openPicker: AddPicker = AddPicker.NONE,
    val showErrors: Boolean = false,
) {
    private val amountValid: Boolean get() = (amountDigits.toLongOrNull() ?: 0L) > 0L

    val visibleCategories: List<Category> get() = categories.filter { it.isIncome == isIncome }

    val isValid: Boolean
        get() = amountValid &&
            selectedCategoryId != null &&
            selectedAccountId != null &&
            comment.isNotBlank()

    val form: AddTransactionFormState
        get() = AddTransactionFormState(
            amountText = formatAmount(amountDigits),
            category = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: NOT_SELECTED,
            dateText = date.format(addDateFormatter),
            timeText = time.format(addTimeFormatter),
            account = accounts.firstOrNull { it.id == selectedAccountId }?.name ?: NOT_SELECTED,
            comment = comment,
            amountError = showErrors && !amountValid,
            categoryError = showErrors && selectedCategoryId == null,
            accountError = showErrors && selectedAccountId == null,
            commentError = showErrors && comment.isBlank(),
        )
}

private fun formatAmount(digits: String): String {
    val value = digits.ifEmpty { "0" }
    return "$value ₽"
}

data class AddTransactionFormState(
    val amountText: String = "0 ₽",
    val category: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val account: String = "",
    val comment: String = "",
    val amountError: Boolean = false,
    val categoryError: Boolean = false,
    val accountError: Boolean = false,
    val commentError: Boolean = false,
)
