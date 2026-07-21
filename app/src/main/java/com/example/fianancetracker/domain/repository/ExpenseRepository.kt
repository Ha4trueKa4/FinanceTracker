package com.example.fianancetracker.domain.repository

import com.example.fianancetracker.domain.expense.Expense

interface ExpenseRepository {
    suspend fun getExpenses(): List<Expense>
}