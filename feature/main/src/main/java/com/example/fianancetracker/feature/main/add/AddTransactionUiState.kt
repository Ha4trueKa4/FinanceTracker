package com.example.fianancetracker.feature.main.add

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.category.Category
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Какой из вложенных пикеров сейчас открыт поверх формы добавления. */
enum class AddPicker { NONE, CATEGORY, ACCOUNT, DATE, TIME }

private const val MAX_DIGITS = 12

/** Плейсхолдер в строках Статья/Счёт, пока пользователь ничего не выбрал. */
private const val NOT_SELECTED = "Не выбрано"

private val addDateFormatter =
    DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))
private val addTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Состояние экрана добавления операции: справочники (статьи/счета), текущий выбор,
 * вводимая сумма, дата/время и открытый пикер. Готовую к рендеру [form] считает сам стейт —
 * composable остаётся «тупым».
 */
data class AddTransactionUiState(
    val visible: Boolean = false,
    val loading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedAccountId: String? = null,
    val amountDigits: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val openPicker: AddPicker = AddPicker.NONE,
    /** Показывать ли ошибки незаполненных полей (взводится при попытке подтвердить). */
    val showErrors: Boolean = false,
) {
    private val amountValid: Boolean get() = (amountDigits.toLongOrNull() ?: 0L) > 0L

    /** Все обязательные поля заполнены — можно отправлять. */
    val isValid: Boolean
        get() = amountValid && selectedCategoryId != null && selectedAccountId != null

    val form: AddTransactionFormState
        get() = AddTransactionFormState(
            amountText = formatAmount(amountDigits),
            category = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: NOT_SELECTED,
            dateText = date.format(addDateFormatter),
            timeText = time.format(addTimeFormatter),
            account = accounts.firstOrNull { it.id == selectedAccountId }?.name ?: NOT_SELECTED,
            amountError = showErrors && !amountValid,
            categoryError = showErrors && selectedCategoryId == null,
            accountError = showErrors && selectedAccountId == null,
        )
}

/** Дописывает цифру к сумме с ограничением длины. */
fun appendKey(current: String, key: String): String {
    if (current.length >= MAX_DIGITS) return current
    return current + key
}

private fun formatAmount(digits: String): String {
    val value = digits.ifEmpty { "0" }
    return "$value ₽"
}
