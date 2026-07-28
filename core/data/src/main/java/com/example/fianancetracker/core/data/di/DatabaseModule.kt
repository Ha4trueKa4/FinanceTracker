package com.example.fianancetracker.core.data.di

import androidx.room.Room
import com.example.fianancetracker.core.data.db.FinanceDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            FinanceDatabase::class.java,
            "finance.db",
        ).fallbackToDestructiveMigration(true).build()
    }

    single { get<FinanceDatabase>().accountDao() }
    single { get<FinanceDatabase>().categoryDao() }
    single { get<FinanceDatabase>().transactionDao() }
    single { get<FinanceDatabase>().pendingSyncDao() }
}
