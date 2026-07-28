package com.example.fianancetracker.core.data.sync

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.dao.PendingSyncDao
import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.mapper.toEntity
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.TransactionRequestDto
import com.example.fianancetracker.core.domain.common.AppError
import com.google.gson.Gson

class SyncManager(
    private val api: FinanceApi,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val pendingSyncDao: PendingSyncDao,
    private val gson: Gson,
) {

    suspend fun syncNow(): Result<Unit> {
        val push = pushPending()
        if (push.isFailure) return push
        pullAccounts()
        pullCategories()
        return pullTransactions()
    }

    suspend fun pushPending(): Result<Unit> {
        val ops = pendingSyncDao.getAll()
        for (op in ops) {
            val result = runOp(op.type, op.targetId, op.payload)
            result.fold(
                onSuccess = { pendingSyncDao.deleteById(op.localId) },
                onFailure = { error ->
                    if (error is AppError.NoInternet) return Result.failure(error)
                    pendingSyncDao.deleteById(op.localId)
                }
            )
        }
        return Result.success(Unit)
    }

    private suspend fun runOp(type: String, localId: Long, payload: String): Result<Unit> =
        when (type) {
            PendingType.CREATE_TRANSACTION.name -> apiCall {
                val dto = gson.fromJson(payload, TransactionRequestDto::class.java)
                val created = api.createTransaction(dto).bodyOrThrow()
                transactionDao.markSynced(localId, created.id)
                Unit
            }

            PendingType.UPDATE_TRANSACTION.name -> {
                val serverId = transactionDao.getByLocalId(localId)?.serverId
                if (serverId == null) {
                    Result.failure(AppError.NoInternet)
                } else apiCall {
                    val dto = gson.fromJson(payload, TransactionRequestDto::class.java)
                    api.updateTransaction(serverId, dto).bodyOrThrow()
                    transactionDao.getByLocalId(localId)?.let {
                        transactionDao.update(it.copy(synced = true))
                    }
                    Unit
                }
            }

            else -> Result.success(Unit)
        }

    suspend fun pullAccounts(): Result<Unit> = apiCall {
        val accounts = api.getAccounts().bodyOrThrow().map { it.toEntity() }
        accountDao.upsertAll(accounts)
        Unit
    }

    suspend fun pullCategories(): Result<Unit> = apiCall {
        val categories = api.getCategories().bodyOrThrow().map { it.toEntity() }
        categoryDao.upsertAll(categories)
        Unit
    }

    suspend fun pullTransactions(): Result<Unit> = apiCall {
        val accounts = api.getAccounts().bodyOrThrow()
        accountDao.upsertAll(accounts.map { it.toEntity() })

        val categories = mutableMapOf<Long, CategoryEntity>()
        val serverIds = mutableListOf<Long>()
        for (account in accounts) {
            val transactions = api.getTransactionsByAccountAndPeriod(account.id).body().orEmpty()
            for (tx in transactions) {
                serverIds += tx.id
                categories[tx.category.id] = CategoryEntity(
                    id = tx.category.id,
                    name = tx.category.name,
                    emoji = tx.category.emoji,
                    isIncome = tx.category.isIncome,
                )
                val existing = transactionDao.getByServerId(tx.id)
                if (existing == null) {
                    transactionDao.insert(tx.toEntity())
                } else {
                    transactionDao.update(tx.toEntity(localId = existing.localId))
                }
            }
        }

        if (categories.isNotEmpty()) categoryDao.upsertAll(categories.values.toList())
        if (serverIds.isEmpty()) transactionDao.deleteAllSynced()
        else transactionDao.deleteSyncedNotIn(serverIds)
        Unit
    }
}
