package com.example.fianancetracker.data.mapper

import com.example.fianancetracker.data.network.dto.AccountDto
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.account.AccountType

fun AccountDto.toDomain(): Account {
    return Account(
        id = id.toString(),
        name = name,
        balance = balance.toDoubleOrNull() ?: 0.0,
        cardNumber = "•••• 0000",
        type = AccountType.DEBIT
    )
}