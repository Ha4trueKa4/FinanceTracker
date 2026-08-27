package com.example.fianancetracker.core.data.di

import com.example.fianancetracker.core.data.repository.PinRepositoryImpl
import com.example.fianancetracker.core.data.repository.SettingsRepositoryImpl
import com.example.fianancetracker.core.domain.repository.PinRepository
import com.example.fianancetracker.core.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsDataModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
    single<PinRepository> { PinRepositoryImpl(androidContext()) }
}
