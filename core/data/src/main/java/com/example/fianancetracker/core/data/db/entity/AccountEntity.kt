package com.example.fianancetracker.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val emoji: String,
    val balance: Double,
    val currency: String,
)
