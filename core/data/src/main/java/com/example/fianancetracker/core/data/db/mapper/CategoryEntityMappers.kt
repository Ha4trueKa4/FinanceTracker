package com.example.fianancetracker.core.data.db.mapper

import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.network.dto.CategoryDto
import com.example.fianancetracker.core.domain.category.Category

fun CategoryDto.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    emoji = emoji,
    isIncome = isIncome,
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    emoji = emoji,
    isIncome = isIncome,
)
