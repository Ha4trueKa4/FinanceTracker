package com.example.fianancetracker.core.data.datasource.local

import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.entity.CategoryEntity

/** Локальный источник данных по категориям: обёртка над [CategoryDao] (Room). */
internal interface CategoryLocalDataSource {
    suspend fun getAll(): List<CategoryEntity>
    suspend fun getById(id: Long): CategoryEntity?
    suspend fun upsertAll(entities: List<CategoryEntity>)
}

internal class CategoryLocalDataSourceImpl(private val dao: CategoryDao) : CategoryLocalDataSource {
    override suspend fun getAll(): List<CategoryEntity> = dao.getAll()
    override suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)
    override suspend fun upsertAll(entities: List<CategoryEntity>) = dao.upsertAll(entities)
}
