package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.category.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}
