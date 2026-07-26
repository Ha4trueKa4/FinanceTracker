package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toDomain
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val api: FinanceApi
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> = apiCall {
        api.getCategories().bodyOrThrow().map { it.toDomain() }
    }
}
