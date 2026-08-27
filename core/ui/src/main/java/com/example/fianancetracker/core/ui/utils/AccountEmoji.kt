package com.example.fianancetracker.core.ui.utils

import androidx.annotation.StringRes
import com.example.fianancetracker.core.domain.account.AccountType
import com.example.fianancetracker.core.ui.R

fun accountEmoji(type: AccountType): String = when (type) {
    AccountType.DEBIT -> "🏦"
    AccountType.SAVINGS -> "🐖"
    AccountType.CREDIT -> "💳"
    AccountType.CASH -> "💵"
}

@StringRes
fun accountTypeTitleRes(type: AccountType): Int = when (type) {
    AccountType.DEBIT -> R.string.account_type_debit
    AccountType.CREDIT -> R.string.account_type_credit
    AccountType.SAVINGS -> R.string.account_type_savings
    AccountType.CASH -> R.string.account_type_cash
}
