package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.mapper.toDomain
import com.example.fianancetracker.data.mapper.toExpense
import com.example.fianancetracker.data.mapper.toIncome
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.analytics.AnalyticsData
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income
import com.example.fianancetracker.domain.repository.AnalyticsRepository

class AnalyticsRepositoryImpl(
    private val api: FinanceApi
) : AnalyticsRepository {

    override suspend fun getAnalyticsData(): AnalyticsData {
        val accountsResponse = api.getAccounts()
        if (!accountsResponse.isSuccessful) {
            throw Exception("Ошибка загрузки счетов: ${accountsResponse.code()}")
        }
        val accountDtos = accountsResponse.body() ?: emptyList()
        val accounts = accountDtos.map { it.toDomain() }

        val allExpenses = mutableListOf<Expense>()
        val allIncomes = mutableListOf<Income>()

        for (accountDto in accountDtos) {
            val txResponse = api.getTransactionsByAccountAndPeriod(accountDto.id)
            if (!txResponse.isSuccessful) continue

            txResponse.body()?.forEach { tx ->
                if (tx.category.isIncome) {
                    allIncomes.add(tx.toIncome())
                } else {
                    allExpenses.add(tx.toExpense())
                }
            }
        }

        val categories = (allExpenses.map { it.category } + allIncomes.map { it.category })
            .distinctBy { it.id }

        return AnalyticsData(
            accounts = accounts,
            expenses = allExpenses,
            incomes = allIncomes,
            categories = categories
        )
    }
}