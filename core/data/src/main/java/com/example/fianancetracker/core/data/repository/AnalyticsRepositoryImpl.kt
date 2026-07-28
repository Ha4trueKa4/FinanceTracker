package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrNull
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toDomain
import com.example.fianancetracker.core.data.mapper.toExpense
import com.example.fianancetracker.core.data.mapper.toIncome
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.domain.analytics.AnalyticsData
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository

class AnalyticsRepositoryImpl(
    private val api: FinanceApi
) : AnalyticsRepository {

    override suspend fun getAnalyticsData(): Result<AnalyticsData> = apiCall {
        val accountDtos = api.getAccounts().bodyOrThrow()
        val accounts = accountDtos.map { it.toDomain() }

        val allExpenses = mutableListOf<Expense>()
        val allIncomes = mutableListOf<Income>()

        for (accountDto in accountDtos) {
            api.getTransactionsByAccountAndPeriod(accountDto.id).bodyOrNull()?.forEach { tx ->
                if (tx.category.isIncome) {
                    allIncomes.add(tx.toIncome())
                } else {
                    allExpenses.add(tx.toExpense())
                }
            }
        }

        val categories = (allExpenses.map { it.category } + allIncomes.map { it.category })
            .distinctBy { it.id }

        AnalyticsData(
            accounts = accounts,
            expenses = allExpenses,
            incomes = allIncomes,
            categories = categories
        )
    }
}
