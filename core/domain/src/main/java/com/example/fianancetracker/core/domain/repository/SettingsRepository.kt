package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
