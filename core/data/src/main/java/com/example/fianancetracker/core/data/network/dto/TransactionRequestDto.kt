package com.example.fianancetracker.core.data.network.dto

data class TransactionRequestDto(
    val accountId: Long,
    val categoryId: Long,
    val amount: String,
    val transactionDate: String,
    val comment: String,
)

data class TransactionDto(
    val id: Long,
    val accountId: Long,
    val categoryId: Long,
    val amount: String,
    val transactionDate: String,
    val comment: String?,
    val createdAt: String,
    val updatedAt: String,
)
