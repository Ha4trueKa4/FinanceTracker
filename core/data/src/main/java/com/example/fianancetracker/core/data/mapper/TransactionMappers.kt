package com.example.fianancetracker.core.data.mapper

import com.example.fianancetracker.core.data.network.dto.TransactionRequestDto
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.domain.transaction.TransactionDetails
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun TransactionResponseDto.toCategory(): Category {
    return Category(
        id = category.id,
        name = category.name,
        emoji = category.emoji,
        isIncome = category.isIncome
    )
}

fun TransactionResponseDto.toExpense(): Expense {
    val amount = (amount.toDoubleOrNull() ?: 0.0)
    val cat = toCategory()
    val formattedDate = try {
        LocalDate.parse(transactionDate.take(10)).format(dateFormatter)
    } catch (e: Exception) {
        transactionDate.take(10)
    }

    return Expense(
        id = id.toString(),
        title = comment ?: cat.name,
        amount = amount,
        category = cat,
        date = formattedDate,
        description = comment ?: "",
        accountName = account.name
    )
}

fun TransactionResponseDto.toIncome(): Income {
    val amount = (amount.toDoubleOrNull() ?: 0.0)
    val cat = toCategory()
    val formattedDate = try {
        LocalDate.parse(transactionDate.take(10)).format(dateFormatter)
    } catch (e: Exception) {
        transactionDate.take(10)
    }

    return Income(
        id = id.toString(),
        title = comment ?: cat.name,
        amount = amount,
        category = cat,
        date = formattedDate,
        description = comment ?: "",
        accountName = account.name
    )
}

fun TransactionResponseDto.toTransaction(): Transaction =
    if (category.isIncome) toIncome() else toExpense()

fun TransactionResponseDto.toDetails(): TransactionDetails {
    val local = LocalDateTime.ofInstant(Instant.parse(transactionDate), ZoneId.systemDefault())
    return TransactionDetails(
        id = id.toString(),
        accountId = account.id,
        categoryId = category.id,
        amount = amount.toDoubleOrNull() ?: 0.0,
        date = local.toLocalDate(),
        time = local.toLocalTime(),
        comment = comment,
        isIncome = category.isIncome,
    )
}

fun TransactionDraft.toRequestDto(): TransactionRequestDto = TransactionRequestDto(
    accountId = accountId,
    categoryId = categoryId,
    amount = BigDecimal.valueOf(amount).toPlainString(),
    transactionDate = transactionDate,
    comment = comment,
)
