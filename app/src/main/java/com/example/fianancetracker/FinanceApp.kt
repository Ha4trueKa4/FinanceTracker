package com.example.fianancetracker

import android.app.Application
import com.example.fianancetracker.core.data.di.networkModule
import com.example.fianancetracker.core.data.di.repositoryModule
import com.example.fianancetracker.feature.analytics.di.analyticsModule
import com.example.fianancetracker.feature.main.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinanceApp)
            modules(
                networkModule,
                repositoryModule,
                mainModule,
                analyticsModule,
            )
        }
    }
}
