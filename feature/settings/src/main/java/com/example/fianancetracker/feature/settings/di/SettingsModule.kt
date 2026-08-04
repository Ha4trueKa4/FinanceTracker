package com.example.fianancetracker.feature.settings.di

import com.example.fianancetracker.feature.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
}
