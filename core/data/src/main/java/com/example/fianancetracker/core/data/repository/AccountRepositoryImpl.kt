package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toDomain
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val api: FinanceApi
) : AccountRepository {

    override suspend fun getAccounts(): Result<List<Account>> = apiCall {
        api.getAccounts().bodyOrThrow().map { it.toDomain() }
    }

    override suspend fun createAccount(
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = apiCall {
        api.createAccount(
            AccountRequestDto(
                name = name,
                emoji = emoji,
                balance = balance.toLong().toString(),
                currency = currency,
            )
        ).bodyOrThrow().toDomain()
    }

    override suspend fun updateAccount(
        id: String,
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = apiCall {
        api.updateAccount(
            id = id.toLong(),
            body = AccountRequestDto(
                name = name,
                emoji = emoji,
                balance = balance.toLong().toString(),
                currency = currency,
            )
        ).bodyOrThrow().toDomain()
    }
}
