package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.account.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    /** Реактивный поток счетов из локальной БД. */
    fun observeAccounts(): Flow<List<Account>>

    /** Дёрнуть сеть: отправить отложенные операции и подтянуть счета с сервера. */
    suspend fun refresh(): Result<Unit>

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
