package com.example.fianancetracker.core.data.mapper

import com.example.fianancetracker.core.data.network.dto.CategoryDto
import com.example.fianancetracker.core.domain.category.Category

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    emoji = emoji,
    isIncome = isIncome,
)
