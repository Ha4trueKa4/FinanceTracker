package com.example.fianancetracker.domain.income

import com.example.fianancetracker.domain.category.Category

data class Income(
    val id: String,
    val title: String,
    val amount: Double,
    val category: Category,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)