package com.example.fianancetracker.core.domain.transaction

import java.time.LocalDate
import java.time.LocalTime

data class TransactionDetails(
    val id: String,
    val accountId: Long,
    val categoryId: Long,
    val amount: Double,
    val date: LocalDate,
    val time: LocalTime,
    val comment: String?,
    val isIncome: Boolean,
)
