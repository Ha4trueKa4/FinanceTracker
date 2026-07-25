package com.example.fianancetracker.core.domain.transaction

import com.example.fianancetracker.core.domain.category.Category

interface Transaction {
    val id: String
    val title: String
    val amount: Double
    val category: Category
    val date: String
    val accountName: String
}