package com.example.fianancetracker.core.data.di

import androidx.room.Room
import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSourceImpl
import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSourceImpl
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSourceImpl
import com.example.fianancetracker.core.data.datasource.local.TransactionLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.TransactionLocalDataSourceImpl
import com.example.fianancetracker.core.data.db.FinanceDatabase
import com.example.fianancetracker.core.data.db.MIGRATION_2_3
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            FinanceDatabase::class.java,
            "finance.db",
        ).addMigrations(MIGRATION_2_3).build()
    }

    single { get<FinanceDatabase>().accountDao() }
    single { get<FinanceDatabase>().categoryDao() }
    single { get<FinanceDatabase>().transactionDao() }
    single { get<FinanceDatabase>().pendingSyncDao() }

    single<AccountLocalDataSource> { AccountLocalDataSourceImpl(get()) }
    single<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }
    single<CategoryLocalDataSource> { CategoryLocalDataSourceImpl(get()) }
    single<PendingSyncLocalDataSource> { PendingSyncLocalDataSourceImpl(get()) }
}
