package com.example.fianancetracker.core.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.fianancetracker.core.data.db.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("SELECT MIN(id) FROM accounts")
    suspend fun minId(): Long?

    @Query("UPDATE accounts SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    // Сверка после создания на сервере: временный локальный id → серверный, счёт становится synced.
    @Query("UPDATE accounts SET id = :newId, serverId = :newId, synced = 1 WHERE id = :oldId")
    suspend fun reassignId(oldId: Long, newId: Long)

    @Upsert
    suspend fun upsert(entity: AccountEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AccountEntity>)
}
