package com.example.fianancetracker.core.domain.transaction

data class TransactionDraft(
    val accountId: Long,
    val categoryId: Long,
    val amount: Double,
    val transactionDate: String,
    val comment: String,
)
