package com.example.fianancetracker

import android.app.Application
import com.example.fianancetracker.core.data.di.databaseModule
import com.example.fianancetracker.core.data.di.networkModule
import com.example.fianancetracker.core.data.di.repositoryModule
import com.example.fianancetracker.core.data.di.syncModule
import com.example.fianancetracker.core.data.sync.ConnectivityObserver
import com.example.fianancetracker.core.data.sync.SyncScheduler
import com.example.fianancetracker.feature.analytics.di.analyticsModule
import com.example.fianancetracker.feature.main.di.mainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class FinanceApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinanceApp)
            workManagerFactory()
            modules(
                networkModule,
                databaseModule,
                syncModule,
                repositoryModule,
                mainModule,
                analyticsModule,
            )
        }

        SyncScheduler.schedulePeriodic(this)
        observeConnectivity()
    }

    private fun observeConnectivity() {
        val observer = get<ConnectivityObserver>()
        appScope.launch {
            observer.observe()
                .drop(1)
                .filter { online -> online }
                .onEach { SyncScheduler.syncNow(this@FinanceApp) }
                .collect {}
        }
    }
}
