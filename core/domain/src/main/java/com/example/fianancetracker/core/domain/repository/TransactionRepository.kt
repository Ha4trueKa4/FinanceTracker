package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft

interface TransactionRepository {
    suspend fun getTransactions(isIncome: Boolean): Result<List<Transaction>>

    suspend fun getTransactionDetails(id: String): Result<TransactionDetails>

    suspend fun createTransaction(draft: TransactionDraft): Result<Unit>

    suspend fun updateTransaction(id: String, draft: TransactionDraft): Result<Unit>
}
