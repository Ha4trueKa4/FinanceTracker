package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.account.Account

interface AccountRepository {
    suspend fun getAccounts(): Result<List<Account>>

    suspend fun createAccount(
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account>

    suspend fun updateAccount(
        id: String,
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account>
}
