package com.example.fianancetracker.core.data.db.mapper

import com.example.fianancetracker.core.data.db.entity.AccountEntity
import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.account.AccountType

fun AccountDto.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    emoji = emoji,
    balance = balance.toDoubleOrNull() ?: 0.0,
    currency = currency,
    serverId = id,
    synced = true,
)

fun AccountEntity.toDomain(): Account = Account(
    id = id.toString(),
    name = name,
    emoji = emoji,
    balance = balance,
    currency = currency,
    cardNumber = "•••• 0000",
    type = AccountType.DEBIT,
)
