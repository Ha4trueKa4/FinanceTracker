package com.example.fianancetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fianancetracker.di.networkModule
import com.example.fianancetracker.di.repositoryModule
import com.example.fianancetracker.di.viewModelModule
import com.example.fianancetracker.presentation.screens.MainScreen
import com.example.fianancetracker.presentation.theme.FiananceTrackerTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidContext(this@MainActivity)
            modules(networkModule, repositoryModule, viewModelModule)
        }

        setContent {
            FiananceTrackerTheme {
                MainScreen()
            }
        }
    }
}