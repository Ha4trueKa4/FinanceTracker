package com.example.fianancetracker.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.dao.PendingSyncDao
import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.entity.AccountEntity
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity
import com.example.fianancetracker.core.data.db.entity.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        PendingSyncEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun pendingSyncDao(): PendingSyncDao
}
