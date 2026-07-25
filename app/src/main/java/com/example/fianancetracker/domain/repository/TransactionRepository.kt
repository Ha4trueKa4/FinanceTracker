package com.example.fianancetracker.domain.repository

import com.example.fianancetracker.domain.transaction.Transaction

interface TransactionRepository {
    /** @param isIncome true — доходы, false — расходы. */
    suspend fun getTransactions(isIncome: Boolean): Result<List<Transaction>>
}
