package com.example.fianancetracker.core.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v2 → v3: у счетов появились serverId (id на сервере) и synced (есть ли неотправленные правки)
// для оффлайн-создания/редактирования. Существующие счета уже синхронизированы: serverId = id.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE accounts ADD COLUMN serverId INTEGER")
        db.execSQL("ALTER TABLE accounts ADD COLUMN synced INTEGER NOT NULL DEFAULT 1")
        db.execSQL("UPDATE accounts SET serverId = id")
    }
}
