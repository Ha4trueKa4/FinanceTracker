package com.example.fianancetracker.data.mapper

import com.example.fianancetracker.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.domain.category.Category
import com.example.fianancetracker.domain.expense.Expense
import com.example.fianancetracker.domain.income.Income
import java.time.LocalDate
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
        description = comment ?: ""
    )
}