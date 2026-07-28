package com.example.fianancetracker.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fianancetracker.core.data.db.entity.TransactionEntity

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE isIncome = :isIncome ORDER BY transactionDate DESC")
    suspend fun getByIncome(isIncome: Boolean): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE localId = :localId")
    suspend fun getByLocalId(localId: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE serverId = :serverId")
    suspend fun getByServerId(serverId: Long): TransactionEntity?

    @Insert
    suspend fun insert(entity: TransactionEntity): Long

    @Update
    suspend fun update(entity: TransactionEntity)

    @Query("UPDATE transactions SET serverId = :serverId, synced = 1 WHERE localId = :localId")
    suspend fun markSynced(localId: Long, serverId: Long)

    @Query("DELETE FROM transactions WHERE synced = 1 AND serverId NOT IN (:serverIds)")
    suspend fun deleteSyncedNotIn(serverIds: List<Long>)

    @Query("DELETE FROM transactions WHERE synced = 1")
    suspend fun deleteAllSynced()
}
