package com.example.fianancetracker.di

import com.example.fianancetracker.presentation.viewmodel.AccountsViewModel
import com.example.fianancetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.fianancetracker.presentation.viewmodel.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // isIncome передаётся из экрана через parametersOf(...)
    viewModel { params -> TransactionsViewModel(isIncome = params.get(), get()) }
    viewModel { AccountsViewModel(get()) }
    viewModel { AnalyticsViewModel(get()) }
}
