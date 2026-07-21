package com.example.fianancetracker.domain.category

data class Category(
    val id: Long,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)