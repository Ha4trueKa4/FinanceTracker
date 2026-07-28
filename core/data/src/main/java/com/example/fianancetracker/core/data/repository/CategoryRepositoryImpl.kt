package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val syncManager: SyncManager,
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        val pull = syncManager.pullCategories()
        val cached = categoryDao.getAll().map { it.toDomain() }
        return if (cached.isNotEmpty()) Result.success(cached) else pull.map { cached }
    }
}
