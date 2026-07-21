package com.example.fianancetracker.domain.expense

import com.example.fianancetracker.domain.category.Category

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: Category,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)