package com.example.fianancetracker.feature.main.add.account

data class AddAccountFormState(
    val amountText: String = "0 ₽",
    val name: String = "",
    val emoji: String = "",
    val currency: String = "",
    val nameError: Boolean = false,
    val amountError: Boolean = false,
)
