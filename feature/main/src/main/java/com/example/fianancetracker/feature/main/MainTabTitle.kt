package com.example.fianancetracker.feature.main

internal val MainTab.title: String
    get() = when (this) {
        MainTab.EXPENSES -> "Расходы"
        MainTab.INCOMES -> "Доходы"
        MainTab.ACCOUNTS -> "Счета"
    }
