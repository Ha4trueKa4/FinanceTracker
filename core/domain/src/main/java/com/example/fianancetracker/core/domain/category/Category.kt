package com.example.fianancetracker.core.domain.category

data class Category(
    val id: Long,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)
