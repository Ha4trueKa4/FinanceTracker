package com.example.fianancetracker.core.data.db.mapper

import com.example.fianancetracker.core.data.network.dto.AccountBriefDto
import com.example.fianancetracker.core.data.network.dto.CategoryDto
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.core.domain.expense.Expense
import com.example.fianancetracker.core.domain.income.Income
import com.example.fianancetracker.core.data.db.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionEntityMappersTest {

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

    private fun entity(
        isIncome: Boolean = false,
        comment: String? = "Кофе",
        amount: Double = 1500.50,
        date: String = "2026-08-03T10:15:30.000Z",
    ) = TransactionEntity(
        localId = 5,
        serverId = 42,
        accountId = 1,
        accountName = "Основной",
        categoryId = 7,
        categoryName = "Еда",
        categoryEmoji = "🍔",
        isIncome = isIncome,
        amount = amount,
        transactionDate = date,
        comment = comment,
        synced = true,
    )

    @Test
    fun `dto toEntity maps fields and marks synced`() {
        val e = dto().toEntity(localId = 5)

        assertEquals(5L, e.localId)
        assertEquals(42L, e.serverId)
        assertEquals(1, e.accountId)
        assertEquals("Основной", e.accountName)
        assertEquals(7, e.categoryId)
        assertEquals("Еда", e.categoryName)
        assertEquals(false, e.isIncome)
        assertEquals(1500.50, e.amount, 0.0001)
        assertEquals("Кофе", e.comment)
        assertTrue(e.synced)
    }

    @Test
    fun `dto toEntity falls back to zero on non-numeric amount`() {
        val e = dto(amount = "abc").toEntity()
        assertEquals(0.0, e.amount, 0.0001)
    }

    @Test
    fun `entity toTransaction returns Expense when not income`() {
        val t = entity(isIncome = false).toTransaction()
        assertTrue(t is Expense)
        assertEquals("Кофе", t.title)
        assertEquals(1500.50, t.amount, 0.0001)
        assertEquals("03.08.2026", t.date)
        assertEquals("Основной", t.accountName)
    }

    @Test
    fun `entity toTransaction returns Income when income`() {
        val t = entity(isIncome = true).toTransaction()
        assertTrue(t is Income)
    }

    @Test
    fun `entity toTransaction uses category name when comment blank`() {
        val t = entity(comment = "").toTransaction() as Expense
        assertEquals("Еда", t.title)
        assertEquals("", t.description)
    }

    @Test
    fun `entity toTransaction uses category name when comment null`() {
        val t = entity(comment = null).toTransaction()
        assertEquals("Еда", t.title)
    }

    @Test
    fun `entity toTransaction keeps raw date on unparseable input`() {
        val t = entity(date = "bad").toTransaction()
        assertEquals("bad", t.date)
    }

    @Test
    fun `entity toDetails parses instant into local date and time`() {
        val d = entity(date = "2026-08-03T00:00:00.000Z", comment = "Кофе").toDetails()
        assertEquals("5", d.id)
        assertEquals(1L, d.accountId)
        assertEquals(7L, d.categoryId)
        assertEquals(1500.50, d.amount, 0.0001)
        assertEquals("Кофе", d.comment)
        assertEquals(false, d.isIncome)
    }
}
