package com.example.fianancetracker.domain.expense

import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.transaction.Transaction

data class Expense(
    override val id: String,
    override val title: String,
    override val amount: Double,
    override val category: Category,
    override val date: String,
    val description: String = "",
    val createdAt: String = "",
    override val accountName: String = ""
) : Transaction