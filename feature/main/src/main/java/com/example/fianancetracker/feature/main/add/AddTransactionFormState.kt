package com.example.fianancetracker.feature.main.add

/**
 * Готовое к рендеру состояние экрана добавления/редактирования транзакции.
 * Все значения — уже отформатированные строки; composable только отображает.
 */
data class AddTransactionFormState(
    val amountText: String = "0 ₽",
    val category: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val account: String = "",
    val amountError: Boolean = false,
    val categoryError: Boolean = false,
    val accountError: Boolean = false,
)
