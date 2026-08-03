package com.example.fianancetracker.core.data.db.mapper

import com.example.fianancetracker.core.data.db.entity.AccountEntity
import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.domain.account.AccountType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountEntityMappersTest {

    private fun dto(balance: String = "2500.75") = AccountDto(
        id = 3,
        userId = 1,
        name = "Копилка",
        emoji = "🐷",
        balance = balance,
        currency = "RUB",
        createdAt = "2026-08-03T00:00:00.000Z",
        updatedAt = "2026-08-03T00:00:00.000Z",
    )

    @Test
    fun `dto toEntity maps fields and marks synced`() {
        val e = dto().toEntity()
        assertEquals(3L, e.id)
        assertEquals(3L, e.serverId)
        assertEquals("Копилка", e.name)
        assertEquals("🐷", e.emoji)
        assertEquals(2500.75, e.balance, 0.0001)
        assertEquals("RUB", e.currency)
        assertTrue(e.synced)
    }

    @Test
    fun `dto toEntity falls back to zero on non-numeric balance`() {
        assertEquals(0.0, dto(balance = "n/a").toEntity().balance, 0.0001)
    }

    @Test
    fun `entity toDomain maps fields with debit defaults`() {
        val entity = AccountEntity(
            id = 3,
            name = "Копилка",
            emoji = "🐷",
            balance = 2500.75,
            currency = "RUB",
            serverId = 3,
            synced = true,
        )
        val a = entity.toDomain()
        assertEquals("3", a.id)
        assertEquals("Копилка", a.name)
        assertEquals("🐷", a.emoji)
        assertEquals(2500.75, a.balance, 0.0001)
        assertEquals("RUB", a.currency)
        assertEquals(AccountType.DEBIT, a.type)
    }
}
