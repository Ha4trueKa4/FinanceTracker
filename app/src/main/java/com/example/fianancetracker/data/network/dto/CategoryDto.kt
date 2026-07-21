package com.example.fianancetracker.data.network.dto

data class CategoryDto(
    val id: Long,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)