package com.example.fianancetracker.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fianancetracker.core.domain.repository.SettingsRepository
import com.example.fianancetracker.core.domain.settings.AppLanguage
import com.example.fianancetracker.core.domain.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Персист пользовательских настроек в Preferences DataStore. */
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val languageKey = stringPreferencesKey("app_language")

    override fun observeThemeMode(): Flow<ThemeMode> =
        context.settingsDataStore.data.map { prefs ->
            prefs[themeKey]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs -> prefs[themeKey] = mode.name }
    }

    override fun observeLanguage(): Flow<AppLanguage> =
        context.settingsDataStore.data.map { prefs ->
            prefs[languageKey]?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() } ?: AppLanguage.RU
        }

    override suspend fun setLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { prefs -> prefs[languageKey] = language.name }
    }
}
