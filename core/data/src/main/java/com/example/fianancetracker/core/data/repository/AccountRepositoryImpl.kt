package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSource
import com.example.fianancetracker.core.data.db.entity.AccountEntity
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.common.AppError
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Репозиторий счетов: offline-first поверх локального источника + отложенная синхронизация. */
internal class AccountRepositoryImpl(
    private val local: AccountLocalDataSource,
    private val syncManager: SyncManager,
    private val pendingSync: PendingSyncLocalDataSource,
    private val gson: Gson,
) : AccountRepository {

    override fun observeAccounts(): Flow<List<Account>> =
        local.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun refresh(): Result<Unit> = withContext(Dispatchers.IO) {
        syncManager.pushPending().fold(
            onSuccess = { syncManager.pullAccounts() },
            onFailure = { Result.failure(it) },
        )
    }

    override suspend fun getAccounts(): Result<List<Account>> = withContext(Dispatchers.IO) {
        val pull = syncManager.pullAccounts()
        val cached = local.getAll().map { it.toDomain() }
        if (cached.isNotEmpty()) Result.success(cached) else pull.map { cached }
    }

    // Оффлайн-first: пишем счёт локально с временным отрицательным id и ставим операцию в очередь.
    // Реальный серверный id проставит синхронизация (SyncManager.CREATE_ACCOUNT + reassignId).
    override suspend fun createAccount(
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = withContext(Dispatchers.IO) {
        val tempId = (local.minId() ?: 0L).coerceAtMost(0L) - 1
        val entity = AccountEntity(
            id = tempId,
            name = name,
            emoji = emoji,
            balance = balance,
            currency = currency,
            serverId = null,
            synced = false,
        )
        local.upsert(entity)
        enqueue(PendingType.CREATE_ACCOUNT, tempId, name, emoji, balance, currency)
        syncManager.pushPending()
        Result.success(entity.toDomain())
    }

    override suspend fun updateAccount(
        id: String,
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ): Result<Account> = withContext(Dispatchers.IO) {
        val localId = id.toLong()
        val existing = local.getById(localId)
            ?: return@withContext Result.failure(AppError.Unknown("Счёт не найден"))
        val updated = existing.copy(
            name = name,
            emoji = emoji,
            balance = balance,
            currency = currency,
            synced = false,
        )
        local.upsert(updated)
        enqueue(PendingType.UPDATE_ACCOUNT, localId, name, emoji, balance, currency)
        syncManager.pushPending()
        Result.success(updated.toDomain())
    }

    private suspend fun enqueue(
        type: PendingType,
        targetId: Long,
        name: String,
        emoji: String,
        balance: Double,
        currency: String,
    ) {
        pendingSync.insert(
            PendingSyncEntity(
                type = type.name,
                targetId = targetId,
                payload = gson.toJson(
                    AccountRequestDto(
                        name = name,
                        emoji = emoji,
                        balance = balance.toLong().toString(),
                        currency = currency,
                    )
                ),
                createdAt = System.currentTimeMillis(),
            )
        )
    }
}
