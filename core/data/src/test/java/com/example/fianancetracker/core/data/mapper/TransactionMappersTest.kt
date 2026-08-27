package com.example.fianancetracker.core.data.mapper

import com.example.fianancetracker.core.data.network.dto.AccountBriefDto
import com.example.fianancetracker.core.data.network.dto.CategoryDto
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.domain.transaction.TransactionDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionMappersTest {

    private fun dto(
        amount: String = "1500.50",
        isIncome: Boolean = false,
        comment: String? = "Кофе",
        date: String = "2026-08-03T10:15:30.000Z",
    ) = TransactionResponseDto(
        id = 42,
        account = AccountBriefDto(1, "Основной", "💰", "1000", "RUB"),
        category = CategoryDto(7, "Еда", "🍔", isIncome),
        amount = amount,
        transactionDate = date,
        comment = comment,
        createdAt = date,
        updatedAt = date,
    )

    @Test
    fun `toTransaction returns Expense for expense category`() {
        val t = dto(isIncome = false).toTransaction()
        assertTrue(t is Expense)
        assertEquals("42", t.id)
        assertEquals("Кофе", t.title)
        assertEquals(1500.50, t.amount, 0.0001)
        assertEquals("03.08.2026", t.date)
    }

    @Test
    fun `toTransaction returns Income for income category`() {
        assertTrue(dto(isIncome = true).toTransaction() is Income)
    }

    @Test
    fun `toExpense falls back to category name when comment null`() {
        assertEquals("Еда", dto(comment = null).toExpense().title)
    }

    @Test
    fun `toExpense keeps raw date on unparseable input`() {
        assertEquals("bad", dto(date = "bad").toExpense().date)
    }

    @Test
    fun `toExpense falls back to zero on non-numeric amount`() {
        assertEquals(0.0, dto(amount = "xyz").toExpense().amount, 0.0001)
    }

    @Test
    fun `draft toRequestDto formats amount without scientific notation`() {
        val draft = TransactionDraft(
            accountId = 1,
            categoryId = 7,
            amount = 1234567.89,
            transactionDate = "2026-08-03T10:15:30.000Z",
            comment = "Зарплата",
        )
        val req = draft.toRequestDto()
        assertEquals(1, req.accountId)
        assertEquals(7, req.categoryId)
        assertEquals("1234567.89", req.amount)
        assertEquals("Зарплата", req.comment)
    }
}
