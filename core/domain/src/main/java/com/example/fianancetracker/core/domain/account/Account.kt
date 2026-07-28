package com.example.fianancetracker.core.domain.account

data class Account(
    val id: String,
    val name: String,
    val emoji: String,
    val balance: Double,
    val currency: String,
    val cardNumber: String,
    val type: AccountType
)
