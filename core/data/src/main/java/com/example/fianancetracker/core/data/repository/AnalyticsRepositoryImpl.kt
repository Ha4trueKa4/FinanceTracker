package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.db.mapper.toTransaction
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.analytics.AnalyticsData
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnalyticsRepositoryImpl(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val syncManager: SyncManager,
) : AnalyticsRepository {

    override suspend fun getAnalyticsData(): Result<AnalyticsData> = withContext(Dispatchers.IO) {
        syncManager.pushPending()
        val pull = syncManager.pullTransactions()

        val accounts = accountDao.getAll().map { it.toDomain() }
        val transactions = transactionDao.getAll()
        val expenses = transactions.filter { !it.isIncome }.map { it.toTransaction() as Expense }
        val incomes = transactions.filter { it.isIncome }.map { it.toTransaction() as Income }
        val categories = categoryDao.getAll().map { it.toDomain() }

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
