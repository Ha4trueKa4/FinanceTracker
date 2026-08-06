package com.example.fianancetracker.feature.settings.di

import com.example.fianancetracker.feature.settings.SettingsViewModel
import com.example.fianancetracker.feature.settings.pin.PinMode
import com.example.fianancetracker.feature.settings.pin.PinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { (mode: PinMode) -> PinViewModel(get(), mode) }
}
