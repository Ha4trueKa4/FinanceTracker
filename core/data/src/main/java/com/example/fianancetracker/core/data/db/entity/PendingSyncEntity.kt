package com.example.fianancetracker.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val type: String,
    val targetId: Long,
    val payload: String,
    val createdAt: Long,
)
