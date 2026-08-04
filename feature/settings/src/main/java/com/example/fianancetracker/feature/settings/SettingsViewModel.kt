package com.example.fianancetracker.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianancetracker.core.domain.repository.SettingsRepository
import com.example.fianancetracker.core.domain.settings.AppLanguage
import com.example.fianancetracker.core.domain.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel настроек: отдаёт текущую тему и сохраняет выбор через [SettingsRepository]. */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.observeThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val language: StateFlow<AppLanguage> = settingsRepository.observeLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.RU)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }
}
