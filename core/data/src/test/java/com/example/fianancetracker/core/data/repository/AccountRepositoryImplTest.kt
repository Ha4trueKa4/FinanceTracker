package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSource
import com.example.fianancetracker.core.data.db.entity.AccountEntity
import com.example.fianancetracker.core.data.db.entity.PendingSyncEntity
import com.example.fianancetracker.core.data.sync.SyncManager
import com.example.fianancetracker.core.domain.common.AppError
import com.example.fianancetracker.core.domain.common.DispatcherProvider
import com.google.gson.Gson
import io.mockk.Runs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AccountRepositoryImplTest {

    private lateinit var local: AccountLocalDataSource
    private lateinit var syncManager: SyncManager
    private lateinit var pendingSync: PendingSyncLocalDataSource
    private lateinit var repo: AccountRepositoryImpl

    private val testDispatchers = object : DispatcherProvider {
        private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
        override val io: CoroutineDispatcher = dispatcher
        override val default: CoroutineDispatcher = dispatcher
    }

    private fun account(id: Long, name: String = "Основной") = AccountEntity(
        id = id,
        name = name,
        emoji = "💰",
        balance = 1000.0,
        currency = "RUB",
        serverId = id,
        synced = true,
    )

    @Before
    fun setUp() {
        local = mockk(relaxed = true)
        syncManager = mockk(relaxed = true)
        pendingSync = mockk(relaxed = true)
        repo = AccountRepositoryImpl(local, syncManager, pendingSync, Gson(), testDispatchers)
    }

    @Test
    fun `getAccounts returns cache when local is not empty`() = runTest {
        coEvery { syncManager.pullAccounts() } returns Result.success(Unit)
        coEvery { local.getAll() } returns listOf(account(1), account(2, "Копилка"))

        val result = repo.getAccounts()

        assertTrue(result.isSuccess)
        assertEquals(listOf("Основной", "Копилка"), result.getOrThrow().map { it.name })
    }

    @Test
    fun `getAccounts propagates pull failure when cache is empty`() = runTest {
        val error = AppError.NoInternet
        coEvery { syncManager.pullAccounts() } returns Result.failure(error)
        coEvery { local.getAll() } returns emptyList()

        val result = repo.getAccounts()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `createAccount stores entity with negative temp id and enqueues pending`() = runTest {
        coEvery { local.minId() } returns 0L
        coEvery { local.upsert(any()) } just Runs
        coEvery { pendingSync.insert(any()) } returns 1L
        coEvery { syncManager.pushPending() } returns Result.success(Unit)

        val entitySlot = slot<AccountEntity>()
        val pendingSlot = slot<PendingSyncEntity>()

        val result = repo.createAccount("Новый", "🎁", 500.0, "RUB")

        assertTrue(result.isSuccess)
        coVerify { local.upsert(capture(entitySlot)) }
        coVerify { pendingSync.insert(capture(pendingSlot)) }
        assertEquals(-1L, entitySlot.captured.id)
        assertEquals(false, entitySlot.captured.synced)
        assertEquals(-1L, pendingSlot.captured.targetId)
    }
}
