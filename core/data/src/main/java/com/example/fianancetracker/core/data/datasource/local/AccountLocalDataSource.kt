package com.example.fianancetracker.core.data.datasource.local

import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

/** Локальный источник данных по счетам: обёртка над [AccountDao] (Room). */
interface AccountLocalDataSource {
    fun observeAll(): Flow<List<AccountEntity>>
    suspend fun getAll(): List<AccountEntity>
    suspend fun getById(id: Long): AccountEntity?
    suspend fun minId(): Long?
    suspend fun markSynced(id: Long)
    suspend fun reassignId(oldId: Long, newId: Long)
    suspend fun upsert(entity: AccountEntity)
    suspend fun upsertAll(entities: List<AccountEntity>)
}

class AccountLocalDataSourceImpl(private val dao: AccountDao) : AccountLocalDataSource {
    override fun observeAll(): Flow<List<AccountEntity>> = dao.observeAll()
    override suspend fun getAll(): List<AccountEntity> = dao.getAll()
    override suspend fun getById(id: Long): AccountEntity? = dao.getById(id)
    override suspend fun minId(): Long? = dao.minId()
    override suspend fun markSynced(id: Long) = dao.markSynced(id)
    override suspend fun reassignId(oldId: Long, newId: Long) = dao.reassignId(oldId, newId)
    override suspend fun upsert(entity: AccountEntity) = dao.upsert(entity)
    override suspend fun upsertAll(entities: List<AccountEntity>) = dao.upsertAll(entities)
}
