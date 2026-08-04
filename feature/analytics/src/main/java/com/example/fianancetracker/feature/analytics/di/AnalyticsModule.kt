package com.example.fianancetracker.feature.analytics.di

import com.example.fianancetracker.feature.analytics.AnalyticsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val analyticsModule = module {
    viewModel { AnalyticsViewModel(get(), androidContext()) }
}
