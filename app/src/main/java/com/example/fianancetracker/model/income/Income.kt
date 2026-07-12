package com.example.fianancetracker.model.income


data class Income(
    val id: String,
    val title: String,
    val amount: Double,
    val category: IncomeCategory,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)
