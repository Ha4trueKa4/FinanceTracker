package com.example.fianancetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.fianancetracker.core.domain.repository.SettingsRepository
import com.example.fianancetracker.core.domain.settings.ThemeMode
import com.example.fianancetracker.core.ui.theme.FiananceTrackerTheme
import com.example.fianancetracker.presentation.navigation.MainAppNavGraph
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by settingsRepository.observeThemeMode()
                .collectAsState(initial = ThemeMode.SYSTEM)
            FiananceTrackerTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                MainAppNavGraph(navController = navController)
            }
        }
    }
}
