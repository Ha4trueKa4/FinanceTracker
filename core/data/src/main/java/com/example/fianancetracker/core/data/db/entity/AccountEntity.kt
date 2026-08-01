package com.example.fianancetracker.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    // Локальный идентификатор. Для оффлайн-созданного счёта — временный отрицательный id,
    // при синхронизации заменяется на серверный (см. Synchronizer/SyncManager.reassignId).
    @PrimaryKey val id: Long,
    val name: String,
    val emoji: String,
    val balance: Double,
    val currency: String,
    // null, пока счёт не создан на сервере.
    val serverId: Long? = null,
    // false, пока есть неотправленные локальные изменения.
    val synced: Boolean = true,
)
