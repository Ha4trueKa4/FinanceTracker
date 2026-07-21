package com.example.fianancetracker.domain.account

enum class AccountType(val displayName: String) {
    DEBIT("Дебетовая"),
    CREDIT("Кредитная"),
    SAVINGS("Накопительная"),
    CASH("Наличные")
}