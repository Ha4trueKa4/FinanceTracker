package com.example.fianancetracker.core.data.di

import com.example.fianancetracker.core.data.sync.ConnectivityObserver
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.data.sync.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val syncModule = module {
    single { SyncManager(get(), get(), get(), get(), get(), get(), get(), get()) }
    single { ConnectivityObserver(androidContext()) }

    worker { SyncWorker(androidContext(), get(), get()) }
}
