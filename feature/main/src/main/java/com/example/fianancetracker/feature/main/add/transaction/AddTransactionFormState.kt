package com.example.fianancetracker.feature.main.add.transaction

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
