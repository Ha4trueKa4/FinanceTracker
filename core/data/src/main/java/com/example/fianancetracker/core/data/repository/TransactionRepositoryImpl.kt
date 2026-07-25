package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrNull
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toExpense
import com.example.fianancetracker.core.data.mapper.toIncome
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.example.fianancetracker.core.domain.transaction.Transaction

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
}
