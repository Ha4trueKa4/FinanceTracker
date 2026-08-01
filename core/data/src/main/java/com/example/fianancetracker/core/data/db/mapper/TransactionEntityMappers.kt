package com.example.fianancetracker.core.data.db.mapper

import com.example.fianancetracker.core.data.db.entity.TransactionEntity
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun TransactionResponseDto.toEntity(localId: Long = 0): TransactionEntity = TransactionEntity(
    localId = localId,
    serverId = id,
    accountId = account.id,
    accountName = account.name,
    categoryId = category.id,
    categoryName = category.name,
    categoryEmoji = category.emoji,
    isIncome = category.isIncome,
    amount = amount.toDoubleOrNull() ?: 0.0,
    transactionDate = transactionDate,
    comment = comment,
    synced = true,
)

private fun TransactionEntity.category(): Category = Category(
    id = categoryId,
    name = categoryName,
    emoji = categoryEmoji,
    isIncome = isIncome,
)

private fun TransactionEntity.formattedDate(): String = try {
    LocalDate.parse(transactionDate.take(10)).format(dateFormatter)
} catch (e: Exception) {
    transactionDate.take(10)
}

fun TransactionEntity.toTransaction(): Transaction {
    val cat = category()
    val displayTitle = comment?.ifBlank { cat.name } ?: cat.name
    return if (isIncome) {
        Income(
            id = localId.toString(),
            title = displayTitle,
            amount = amount,
            category = cat,
            date = formattedDate(),
            description = comment.orEmpty(),
            accountName = accountName,
        )
    } else {
        Expense(
            id = localId.toString(),
            title = displayTitle,
            amount = amount,
            category = cat,
            date = formattedDate(),
            description = comment.orEmpty(),
            accountName = accountName,
        )
    }
}

fun TransactionEntity.toDetails(): TransactionDetails {
    val local = try {
        LocalDateTime.ofInstant(Instant.parse(transactionDate), ZoneId.systemDefault())
    } catch (e: Exception) {
        LocalDateTime.now()
    }
    return TransactionDetails(
        id = localId.toString(),
        accountId = accountId,
        categoryId = categoryId,
        amount = amount,
        date = local.toLocalDate(),
        time = local.toLocalTime(),
        comment = comment,
        isIncome = isIncome,
    )
}
