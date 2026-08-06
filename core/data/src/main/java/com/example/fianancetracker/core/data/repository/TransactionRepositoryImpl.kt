package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.TransactionLocalDataSource
import com.example.fianancetracker.core.data.datasource.remote.TransactionRemoteDataSource
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.entity.TransactionEntity
import com.example.fianancetracker.core.data.db.mapper.toDetails
import com.example.fianancetracker.core.data.db.mapper.toTransaction
import com.example.fianancetracker.core.data.mapper.toDetails
import com.example.fianancetracker.core.data.mapper.toRequestDto
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import com.google.gson.Gson
import com.example.fianancetracker.core.domain.common.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Репозиторий транзакций: offline-first, локальный источник + очередь + удалённые детали. */
internal class TransactionRepositoryImpl(
    private val remote: TransactionRemoteDataSource,
    private val local: TransactionLocalDataSource,
    private val accountLocal: AccountLocalDataSource,
    private val categoryLocal: CategoryLocalDataSource,
    private val pendingSync: PendingSyncLocalDataSource,
    private val syncManager: SyncManager,
    private val gson: Gson,
    private val dispatchers: DispatcherProvider,
) : TransactionRepository {

    override fun observeTransactions(
        isIncome: Boolean,
        startDate: String,
        endDate: String,
    ): Flow<List<Transaction>> =
        local.observeByIncomeBetween(isIncome, startDate, endDate)
            .map { list -> list.map { it.toTransaction() } }

    override suspend fun refresh(): Result<Unit> = withContext(dispatchers.io) {
        syncManager.pushPending().fold(
            onSuccess = { syncManager.pullTransactions() },
            onFailure = { Result.failure(it) },
        )
    }

    override suspend fun getTransactionDetails(id: String): Result<TransactionDetails> =
        withContext(dispatchers.io) {
            val cached = local.getByLocalId(id.toLong())
            if (cached != null) return@withContext Result.success(cached.toDetails())

            apiCall { remote.getById(id.toLong()).toDetails() }
        }

    override suspend fun createTransaction(draft: TransactionDraft): Result<Unit> =
        withContext(dispatchers.io) {
            val account = accountLocal.getById(draft.accountId)
            val category = categoryLocal.getById(draft.categoryId)

            val localId = local.insert(
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
            Result.success(Unit)
        }

    override suspend fun updateTransaction(id: String, draft: TransactionDraft): Result<Unit> =
        withContext(dispatchers.io) {
            val localId = id.toLong()
            val existing = local.getByLocalId(localId)
            val account = accountLocal.getById(draft.accountId)
            val category = categoryLocal.getById(draft.categoryId)

            local.update(
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
            Result.success(Unit)
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
        pendingSync.insert(
            PendingSyncEntity(
                type = type.name,
                targetId = targetId,
                payload = gson.toJson(draft.toRequestDto()),
                createdAt = System.currentTimeMillis(),
            )
        )
    }
}
