package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.TransactionLocalDataSource
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.db.mapper.toTransaction
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.analytics.AnalyticsData
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Репозиторий аналитики: синхронизирует и агрегирует локальные данные для экрана аналитики. */
internal class AnalyticsRepositoryImpl(
    private val accountLocal: AccountLocalDataSource,
    private val categoryLocal: CategoryLocalDataSource,
    private val transactionLocal: TransactionLocalDataSource,
    private val syncManager: SyncManager,
) : AnalyticsRepository {

    override suspend fun getAnalyticsData(): Result<AnalyticsData> = withContext(Dispatchers.IO) {
        syncManager.pushPending()
        val pull = syncManager.pullTransactions()

        val accounts = accountLocal.getAll().map { it.toDomain() }
        val transactions = transactionLocal.getAll()
        val expenses = transactions.filter { !it.isIncome }.map { it.toTransaction() as Expense }
        val incomes = transactions.filter { it.isIncome }.map { it.toTransaction() as Income }
        val categories = categoryLocal.getAll().map { it.toDomain() }

        val hasData = accounts.isNotEmpty() || transactions.isNotEmpty() || categories.isNotEmpty()
        val data = AnalyticsData(
            accounts = accounts,
            expenses = expenses,
            incomes = incomes,
            categories = categories,
        )
        if (hasData) Result.success(data) else pull.map { data }
    }
}
