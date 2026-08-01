package com.example.fianancetracker.core.data.datasource.local

import com.example.fianancetracker.core.data.db.dao.PendingSyncDao
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity

/** Локальный источник очереди офлайн-операций: обёртка над [PendingSyncDao] (Room). */
interface PendingSyncLocalDataSource {
    suspend fun getAll(): List<PendingSyncEntity>
    suspend fun insert(entity: PendingSyncEntity): Long
    suspend fun deleteById(localId: Long)
    suspend fun retargetAccountOps(oldId: Long, newId: Long)
}

class PendingSyncLocalDataSourceImpl(private val dao: PendingSyncDao) : PendingSyncLocalDataSource {
    override suspend fun getAll(): List<PendingSyncEntity> = dao.getAll()
    override suspend fun insert(entity: PendingSyncEntity): Long = dao.insert(entity)
    override suspend fun deleteById(localId: Long) = dao.deleteById(localId)
    override suspend fun retargetAccountOps(oldId: Long, newId: Long) =
        dao.retargetAccountOps(oldId, newId)
}
