package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrNull
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toDetails
import com.example.fianancetracker.core.data.mapper.toExpense
import com.example.fianancetracker.core.data.mapper.toIncome
import com.example.fianancetracker.core.data.mapper.toRequestDto
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft

class TransactionRepositoryImpl(
    private val api: FinanceApi
) : TransactionRepository {

    override suspend fun getTransactions(isIncome: Boolean): Result<List<Transaction>> = apiCall {
        val accounts = api.getAccounts().bodyOrThrow()

        accounts.flatMap { account ->
            api.getTransactionsByAccountAndPeriod(account.id).bodyOrNull()
                ?.filter { it.category.isIncome == isIncome }
                ?.sortedByDescending { it.transactionDate }
                ?.map { if (isIncome) it.toIncome() else it.toExpense() }
                .orEmpty()
        }
    }

    override suspend fun getTransactionDetails(id: String): Result<TransactionDetails> = apiCall {
        api.getTransactionById(id.toLong()).bodyOrThrow().toDetails()
    }

    override suspend fun createTransaction(draft: TransactionDraft): Result<Unit> = apiCall {
        api.createTransaction(draft.toRequestDto()).bodyOrThrow()
        Unit
    }

    override suspend fun updateTransaction(id: String, draft: TransactionDraft): Result<Unit> = apiCall {
        api.updateTransaction(id.toLong(), draft.toRequestDto()).bodyOrThrow()
        Unit
    }
}
