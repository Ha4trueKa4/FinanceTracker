package com.example.fianancetracker.core.domain.account

data class Account(
    val id: String,
    val name: String,
    val balance: Double,
    val cardNumber: String,
    val type: AccountType
)

