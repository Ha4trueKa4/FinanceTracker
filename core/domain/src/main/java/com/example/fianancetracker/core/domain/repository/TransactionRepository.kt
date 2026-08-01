package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    /**
     * Реактивный поток транзакций за день [startDate; endDate) из локальной БД.
     * Даты — ISO-строки (yyyy-MM-dd). Обновляется сам при любом изменении Room (в т.ч. после фоновой синхронизации).
     */
    fun observeTransactions(isIncome: Boolean, startDate: String, endDate: String): Flow<List<Transaction>>

    /** Дёрнуть сеть: отправить отложенные операции и подтянуть свежие данные с сервера. */
    suspend fun refresh(): Result<Unit>

    suspend fun getTransactionDetails(id: String): Result<TransactionDetails>

    suspend fun createTransaction(draft: TransactionDraft): Result<Unit>

    suspend fun updateTransaction(id: String, draft: TransactionDraft): Result<Unit>
}
