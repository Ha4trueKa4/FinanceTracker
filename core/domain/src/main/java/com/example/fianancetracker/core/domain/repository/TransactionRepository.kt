package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.transaction.Transaction

interface TransactionRepository {
    /** @param isIncome true — доходы, false — расходы. */
    suspend fun getTransactions(isIncome: Boolean): Result<List<Transaction>>
}
