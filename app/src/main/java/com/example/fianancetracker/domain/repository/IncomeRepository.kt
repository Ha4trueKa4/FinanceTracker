package com.example.fianancetracker.domain.repository

import com.example.fianancetracker.domain.income.Income

interface IncomeRepository {
    suspend fun getIncomes(): List<Income>
}