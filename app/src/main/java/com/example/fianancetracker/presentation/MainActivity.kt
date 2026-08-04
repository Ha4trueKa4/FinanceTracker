package com.example.fianancetracker.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.fianancetracker.core.domain.repository.SettingsRepository
import com.example.fianancetracker.core.domain.settings.AppLanguage
import com.example.fianancetracker.core.domain.settings.ThemeMode
import com.example.fianancetracker.core.ui.theme.FiananceTrackerTheme
import com.example.fianancetracker.presentation.navigation.MainAppNavGraph
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            settingsRepository.observeLanguage().collect(::applyLanguage)
        }

        setContent {
            val themeMode by settingsRepository.observeThemeMode()
                .collectAsState(initial = ThemeMode.SYSTEM)
            FiananceTrackerTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                MainAppNavGraph(navController = navController)
            }
        }
    }

    private fun applyLanguage(language: AppLanguage) {
        val effectiveTag = ConfigurationCompat.getLocales(resources.configuration)[0]?.language
        if (effectiveTag != language.tag) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.tag),
            )
        }
    }
}
