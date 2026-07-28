package com.example.fianancetracker.core.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.fianancetracker.core.data.db.entity.AccountEntity

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Upsert
    suspend fun upsert(entity: AccountEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AccountEntity>)
}
