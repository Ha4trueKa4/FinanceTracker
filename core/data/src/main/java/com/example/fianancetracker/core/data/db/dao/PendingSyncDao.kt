package com.example.fianancetracker.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity

@Dao
interface PendingSyncDao {

    @Query("SELECT * FROM pending_sync ORDER BY localId ASC")
    suspend fun getAll(): List<PendingSyncEntity>

    @Query("SELECT COUNT(*) FROM pending_sync")
    suspend fun count(): Int

    @Insert
    suspend fun insert(entity: PendingSyncEntity): Long

    @Query("DELETE FROM pending_sync WHERE localId = :localId")
    suspend fun deleteById(localId: Long)

    // После сверки id счёта: перевести отложенные операции по счёту на новый серверный id.
    @Query("UPDATE pending_sync SET targetId = :newId WHERE targetId = :oldId AND type = 'UPDATE_ACCOUNT'")
    suspend fun retargetAccountOps(oldId: Long, newId: Long)
}
