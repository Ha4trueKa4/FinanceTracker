package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSource
import com.example.fianancetracker.core.data.db.mapper.toDomain
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Репозиторий категорий: локальный кэш + подтягивание с сервера при пустом кэше. */
class CategoryRepositoryImpl(
    private val local: CategoryLocalDataSource,
    private val syncManager: SyncManager,
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        val pull = syncManager.pullCategories()
        val cached = local.getAll().map { it.toDomain() }
        if (cached.isNotEmpty()) Result.success(cached) else pull.map { cached }
    }
}
