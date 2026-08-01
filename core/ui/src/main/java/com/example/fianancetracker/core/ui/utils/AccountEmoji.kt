package com.example.fianancetracker.core.ui.utils

import com.example.fianancetracker.core.domain.account.AccountType

fun accountEmoji(type: AccountType): String = when (type) {
    AccountType.DEBIT -> "🏦"
    AccountType.SAVINGS -> "🐖"
    AccountType.CREDIT -> "💳"
    AccountType.CASH -> "💵"
}
