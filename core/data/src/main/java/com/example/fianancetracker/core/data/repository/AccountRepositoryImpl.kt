package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.db.mapper.toEntity
import com.example.fianancetracker.core.data.mapper.toDomain as dtoToDomain
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val api: FinanceApi,
    private val accountDao: AccountDao,
    private val syncManager: SyncManager,
) : AccountRepository {

    override suspend fun getAccounts(): Result<List<Account>> = withContext(Dispatchers.IO) {
        val pull = syncManager.pullAccounts()
        val cached = accountDao.getAll().map { it.toDomain() }
        if (cached.isNotEmpty()) Result.success(cached) else pull.map { cached }
    }

    override suspend fun createAccount(
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = apiCall {
        val dto = api.createAccount(
            AccountRequestDto(
                name = name,
                emoji = emoji,
                balance = balance.toLong().toString(),
                currency = currency,
            )
        ).bodyOrThrow()
        accountDao.upsert(dto.toEntity())
        dto.dtoToDomain()
    }

    override suspend fun updateAccount(
        id: String,
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = apiCall {
        val dto = api.updateAccount(
            id = id.toLong(),
            body = AccountRequestDto(
                name = name,
                emoji = emoji,
                balance = balance.toLong().toString(),
                currency = currency,
            )
        ).bodyOrThrow()
        accountDao.upsert(dto.toEntity())
        dto.dtoToDomain()
    }
}
