package com.example.fianancetracker.data.network.dto

data class TransactionResponseDto(
    val id: Long,
    val account: AccountBriefDto,
    val category: CategoryDto,
    val amount: String,
    val transactionDate: String,
    val comment: String?,
    val createdAt: String,
    val updatedAt: String
)