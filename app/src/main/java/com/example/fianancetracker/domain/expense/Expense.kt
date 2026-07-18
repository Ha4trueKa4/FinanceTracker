package com.example.fianancetracker.domain.expense

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)
