package com.example.fianancetracker.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["serverId"], unique = true)],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Long?,
    val accountId: Long,
    val accountName: String,
    val categoryId: Long,
    val categoryName: String,
    val categoryEmoji: String,
    val isIncome: Boolean,
    val amount: Double,
    val transactionDate: String,
    val comment: String?,
    val synced: Boolean,
)
