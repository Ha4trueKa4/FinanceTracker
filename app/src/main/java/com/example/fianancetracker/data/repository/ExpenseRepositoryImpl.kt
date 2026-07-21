package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.mapper.toExpense
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.repository.ExpenseRepository

class ExpenseRepositoryImpl(
    private val api: FinanceApi
) : ExpenseRepository {

    override suspend fun getExpenses(): List<Expense> {
        val accountsResponse = api.getAccounts()
        if (!accountsResponse.isSuccessful) {
            throw Exception("Ошибка загрузки счетов: ${accountsResponse.code()}")
        }
        val accounts = accountsResponse.body() ?: emptyList()

        return accounts.flatMap { account ->
            val txResponse = api.getTransactionsByAccountAndPeriod(account.id)
            if (!txResponse.isSuccessful) return@flatMap emptyList()
            txResponse.body()
                ?.filter { !it.category.isIncome }
                ?.sortedByDescending { it.transactionDate }
                ?.map { it.toExpense() }
                ?: emptyList()
        }
    }
}