package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.common.apiCall
import com.example.fianancetracker.data.common.bodyOrNull
import com.example.fianancetracker.data.common.bodyOrThrow
import com.example.fianancetracker.data.mapper.toExpense
import com.example.fianancetracker.data.mapper.toIncome
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.repository.TransactionRepository
import com.example.fianancetracker.domain.transaction.Transaction

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
