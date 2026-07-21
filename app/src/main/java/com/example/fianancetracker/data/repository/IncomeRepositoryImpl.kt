package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.mapper.toIncome
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.domain.repository.IncomeRepository

class IncomeRepositoryImpl(
    private val api: FinanceApi
) : IncomeRepository {

    override suspend fun getIncomes(): List<Income> {
        val accountsResponse = api.getAccounts()
        if (!accountsResponse.isSuccessful) {
            throw Exception("Ошибка загрузки счетов: ${accountsResponse.code()}")
        }
        val accounts = accountsResponse.body() ?: emptyList()

        return accounts.flatMap { account ->
            val txResponse = api.getTransactionsByAccountAndPeriod(account.id)
            if (!txResponse.isSuccessful) return@flatMap emptyList()
            txResponse.body()
                ?.filter { it.category.isIncome }
                ?.sortedByDescending { it.transactionDate }
                ?.map { it.toIncome() }
                ?: emptyList()
        }
    }
}