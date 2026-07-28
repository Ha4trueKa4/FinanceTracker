package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.dao.PendingSyncDao
import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.entity.TransactionEntity
import com.example.fianancetracker.core.data.db.mapper.toDetails
import com.example.fianancetracker.core.data.db.mapper.toTransaction
import com.example.fianancetracker.core.data.mapper.toDetails
import com.example.fianancetracker.core.data.mapper.toRequestDto
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import com.google.gson.Gson

class TransactionRepositoryImpl(
    private val api: FinanceApi,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val pendingSyncDao: PendingSyncDao,
    private val syncManager: SyncManager,
    private val gson: Gson,
) : TransactionRepository {

    override suspend fun getTransactions(isIncome: Boolean): Result<List<Transaction>> {
        syncManager.pushPending()
        val pull = syncManager.pullTransactions()
        val cached = transactionDao.getByIncome(isIncome).map { it.toTransaction() }
        return if (cached.isNotEmpty()) Result.success(cached) else pull.map { cached }
    }

    override suspend fun getTransactionDetails(id: String): Result<TransactionDetails> {
        val local = transactionDao.getByLocalId(id.toLong())
        if (local != null) return Result.success(local.toDetails())

        return apiCall { api.getTransactionById(id.toLong()).bodyOrThrow().toDetails() }
    }

    override suspend fun createTransaction(draft: TransactionDraft): Result<Unit> {
        val account = accountDao.getById(draft.accountId)
        val category = categoryDao.getById(draft.categoryId)

        val localId = transactionDao.insert(
            entityFor(
                localId = 0,
                serverId = null,
                draft = draft,
                accountName = account?.name,
                category = category,
            )
        )
        enqueue(PendingType.CREATE_TRANSACTION, localId, draft)
        syncManager.pushPending()
        return Result.success(Unit)
    }

    override suspend fun updateTransaction(id: String, draft: TransactionDraft): Result<Unit> {
        val localId = id.toLong()
        val existing = transactionDao.getByLocalId(localId)
        val account = accountDao.getById(draft.accountId)
        val category = categoryDao.getById(draft.categoryId)

        transactionDao.update(
            entityFor(
                localId = localId,
                serverId = existing?.serverId,
                draft = draft,
                accountName = account?.name,
                category = category,
            )
        )
        enqueue(PendingType.UPDATE_TRANSACTION, localId, draft)
        syncManager.pushPending()
        return Result.success(Unit)
    }

    private fun entityFor(
        localId: Long,
        serverId: Long?,
        draft: TransactionDraft,
        accountName: String?,
        category: CategoryEntity?,
    ): TransactionEntity = TransactionEntity(
        localId = localId,
        serverId = serverId,
        accountId = draft.accountId,
        accountName = accountName.orEmpty(),
        categoryId = draft.categoryId,
        categoryName = category?.name.orEmpty(),
        categoryEmoji = category?.emoji.orEmpty(),
        isIncome = category?.isIncome ?: false,
        amount = draft.amount,
        transactionDate = draft.transactionDate,
        comment = draft.comment.ifBlank { null },
        synced = false,
    )

    private suspend fun enqueue(type: PendingType, targetId: Long, draft: TransactionDraft) {
        pendingSyncDao.insert(
            PendingSyncEntity(
                type = type.name,
                targetId = targetId,
                payload = gson.toJson(draft.toRequestDto()),
                createdAt = System.currentTimeMillis(),
            )
        )
    }
}
