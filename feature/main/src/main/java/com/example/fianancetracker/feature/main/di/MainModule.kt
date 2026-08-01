package com.example.fianancetracker.feature.main.di

import com.example.fianancetracker.feature.main.MainViewModel
import com.example.fianancetracker.feature.main.add.account.AddAccountViewModel
import com.example.fianancetracker.feature.main.add.transaction.AddTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { AddTransactionViewModel(get(), get(), get()) }
    viewModel { AddAccountViewModel(get()) }
}
