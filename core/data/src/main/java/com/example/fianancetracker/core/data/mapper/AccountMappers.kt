package com.example.fianancetracker.core.data.mapper

import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.account.AccountType

fun AccountDto.toDomain(): Account {
    return Account(
        id = id.toString(),
        name = name,
        emoji = emoji,
        balance = balance.toDoubleOrNull() ?: 0.0,
        currency = currency,
        cardNumber = "•••• 0000",
        type = AccountType.DEBIT
    )
}
