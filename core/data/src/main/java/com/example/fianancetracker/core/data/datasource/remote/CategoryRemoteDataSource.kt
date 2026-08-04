package com.example.fianancetracker.core.data.datasource.remote

import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.CategoryDto

/** Удалённый источник данных по категориям: тонкая обёртка над [FinanceApi]. */
internal interface CategoryRemoteDataSource {
    suspend fun getCategories(): List<CategoryDto>
}

internal class CategoryRemoteDataSourceImpl(private val api: FinanceApi) : CategoryRemoteDataSource {
    override suspend fun getCategories(): List<CategoryDto> = api.getCategories().bodyOrThrow()
}
