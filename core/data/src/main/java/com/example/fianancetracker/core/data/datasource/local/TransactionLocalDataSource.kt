package com.example.fianancetracker.core.data.datasource.local

import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/** Локальный источник данных по транзакциям: обёртка над [TransactionDao] (Room). */
internal interface TransactionLocalDataSource {
    fun observeByIncomeBetween(
        isIncome: Boolean,
        startDate: String,
        endDate: String,
    ): Flow<List<TransactionEntity>>
    suspend fun getAll(): List<TransactionEntity>
    suspend fun getByLocalId(localId: Long): TransactionEntity?
    suspend fun getByServerId(serverId: Long): TransactionEntity?
    suspend fun insert(entity: TransactionEntity): Long
    suspend fun update(entity: TransactionEntity)
    suspend fun markSynced(localId: Long, serverId: Long)
    suspend fun reassignAccount(oldAccountId: Long, newAccountId: Long)
    suspend fun deleteSyncedNotIn(serverIds: List<Long>)
    suspend fun deleteAllSynced()
}

internal class TransactionLocalDataSourceImpl(private val dao: TransactionDao) : TransactionLocalDataSource {
    override fun observeByIncomeBetween(
        isIncome: Boolean,
        startDate: String,
        endDate: String,
    ): Flow<List<TransactionEntity>> = dao.observeByIncomeBetween(isIncome, startDate, endDate)

    override suspend fun getAll(): List<TransactionEntity> = dao.getAll()
    override suspend fun getByLocalId(localId: Long): TransactionEntity? = dao.getByLocalId(localId)
    override suspend fun getByServerId(serverId: Long): TransactionEntity? = dao.getByServerId(serverId)
    override suspend fun insert(entity: TransactionEntity): Long = dao.insert(entity)
    override suspend fun update(entity: TransactionEntity) = dao.update(entity)
    override suspend fun markSynced(localId: Long, serverId: Long) = dao.markSynced(localId, serverId)
    override suspend fun reassignAccount(oldAccountId: Long, newAccountId: Long) =
        dao.reassignAccount(oldAccountId, newAccountId)
    override suspend fun deleteSyncedNotIn(serverIds: List<Long>) = dao.deleteSyncedNotIn(serverIds)
    override suspend fun deleteAllSynced() = dao.deleteAllSynced()
}
