package com.example.fianancetracker.di

import com.example.fianancetracker.presentation.viewmodel.AccountsViewModel
import com.example.fianancetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.fianancetracker.presentation.viewmodel.ExpensesViewModel
import com.example.fianancetracker.presentation.viewmodel.IncomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ExpensesViewModel(get()) }
    viewModel { IncomeViewModel(get()) }
    viewModel { AccountsViewModel(get()) }
    viewModel { AnalyticsViewModel(get(), get(), get()) }
}