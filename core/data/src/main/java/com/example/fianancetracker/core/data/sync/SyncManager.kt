package com.example.fianancetracker.core.data.sync

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.datasource.local.AccountLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.CategoryLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.PendingSyncLocalDataSource
import com.example.fianancetracker.core.data.datasource.local.TransactionLocalDataSource
import com.example.fianancetracker.core.data.datasource.remote.AccountRemoteDataSource
import com.example.fianancetracker.core.data.datasource.remote.CategoryRemoteDataSource
import com.example.fianancetracker.core.data.datasource.remote.TransactionRemoteDataSource
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.mapper.toEntity
import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto
import com.example.fianancetracker.core.data.network.dto.TransactionRequestDto
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto
import com.example.fianancetracker.core.domain.common.AppError
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Оркестратор синхронизации: пушит очередь офлайн-операций и подтягивает данные с сервера. */
class SyncManager(
    private val accountRemote: AccountRemoteDataSource,
    private val transactionRemote: TransactionRemoteDataSource,
    private val categoryRemote: CategoryRemoteDataSource,
    private val accountLocal: AccountLocalDataSource,
    private val transactionLocal: TransactionLocalDataSource,
    private val categoryLocal: CategoryLocalDataSource,
    private val pendingSync: PendingSyncLocalDataSource,
    private val gson: Gson,
) {

    // Один mutex на все входы синхронизации: воркер, коннективити-триггер и репозитории
    // могут дёрнуть push/sync одновременно — без сериализации одна pending-операция
    // отправилась бы на сервер дважды (дубликат).
    private val syncMutex = Mutex()

    suspend fun syncNow(): Result<Unit> = syncMutex.withLock {
        val push = doPushPending()
        if (push.isFailure) return@withLock push
        pullAccounts()
        pullCategories()
        pullTransactions()
    }

    suspend fun pushPending(): Result<Unit> = syncMutex.withLock { doPushPending() }

    private suspend fun doPushPending(): Result<Unit> {
        val ops = pendingSync.getAll()
        for (op in ops) {
            val result = runOp(op.type, op.targetId, op.payload)
            val error = result.exceptionOrNull()
            when {
                // Успех — операция доехала, убираем из очереди.
                result.isSuccess -> pendingSync.deleteById(op.localId)
                // Постоянная клиентская ошибка (4xx): операция невалидна, повтор не поможет —
                // выкидываем, иначе она навсегда заблокирует очередь.
                error is AppError.Server && error.code in 400..499 ->
                    pendingSync.deleteById(op.localId)
                // Нет сети / 5xx / неизвестная ошибка — НЕ теряем операцию:
                // оставляем в очереди и выходим, воркер повторит попытку позже.
                else -> return Result.failure(error ?: AppError.Unknown(null))
            }
        }
        return Result.success(Unit)
    }

    private suspend fun runOp(type: String, targetId: Long, payload: String): Result<Unit> =
        when (type) {
            PendingType.CREATE_TRANSACTION.name -> apiCall {
                val dto = resolveTxAccount(targetId, payload)
                val created = transactionRemote.create(dto)
                transactionLocal.markSynced(targetId, created.id)
                Unit
            }

            PendingType.UPDATE_TRANSACTION.name -> {
                val serverId = transactionLocal.getByLocalId(targetId)?.serverId
                if (serverId == null) {
                    Result.failure(AppError.NoInternet)
                } else apiCall {
                    val dto = resolveTxAccount(targetId, payload)
                    transactionRemote.update(serverId, dto)
                    transactionLocal.getByLocalId(targetId)?.let {
                        transactionLocal.update(it.copy(synced = true))
                    }
                    Unit
                }
            }

            PendingType.CREATE_ACCOUNT.name -> apiCall {
                val dto = gson.fromJson(payload, AccountRequestDto::class.java)
                val created = accountRemote.createAccount(dto)
                // Сверка: временный локальный id → серверный (счёт, ссылки транзакций, очередь).
                accountLocal.reassignId(oldId = targetId, newId = created.id)
                transactionLocal.reassignAccount(oldAccountId = targetId, newAccountId = created.id)
                pendingSync.retargetAccountOps(oldId = targetId, newId = created.id)
                Unit
            }

            PendingType.UPDATE_ACCOUNT.name -> {
                val serverId = accountLocal.getById(targetId)?.serverId
                if (serverId == null) {
                    // Счёт ещё не создан на сервере — оставляем операцию в очереди до следующего цикла.
                    Result.failure(AppError.Unknown("Счёт ещё не синхронизирован"))
                } else apiCall {
                    val dto = gson.fromJson(payload, AccountRequestDto::class.java)
                    accountRemote.updateAccount(serverId, dto)
                    accountLocal.markSynced(targetId)
                    Unit
                }
            }

            else -> Result.success(Unit)
        }

    // В payload транзакции id счёта мог быть временным (оффлайн-счёт). Берём актуальный
    // id из сущности транзакции — после сверки счёта он уже указывает на серверный.
    private suspend fun resolveTxAccount(txLocalId: Long, payload: String): TransactionRequestDto {
        val dto = gson.fromJson(payload, TransactionRequestDto::class.java)
        val accountId = transactionLocal.getByLocalId(txLocalId)?.accountId ?: dto.accountId
        if (accountId <= 0) throw AppError.Unknown("Счёт транзакции ещё не синхронизирован")
        return dto.copy(accountId = accountId)
    }

    // Не затираем локальные несинхронизированные счета (оффлайн-создание/правку) данными сервера.
    private suspend fun upsertServerAccounts(remote: List<AccountDto>) {
        val localById = accountLocal.getAll().associateBy { it.id }
        val toUpsert = remote.mapNotNull { dto ->
            val local = localById[dto.id]
            if (local != null && !local.synced) null else dto.toEntity()
        }
        if (toUpsert.isNotEmpty()) accountLocal.upsertAll(toUpsert)
    }

    suspend fun pullAccounts(): Result<Unit> = apiCall {
        upsertServerAccounts(accountRemote.getAccounts())
        Unit
    }

    suspend fun pullCategories(): Result<Unit> = apiCall {
        categoryLocal.upsertAll(categoryRemote.getCategories().map { it.toEntity() })
        Unit
    }

    suspend fun pullTransactions(): Result<Unit> = apiCall {
        val accounts = accountRemote.getAccounts()
        upsertServerAccounts(accounts)

        val remote = accounts.flatMap { fetchAccountTransactions(it.id) }
        remote.forEach { upsertTransaction(it) }
        syncCategoriesFrom(remote)
        pruneSyncedTransactions(remote.map { it.id })
        Unit
    }

    // Без явного периода сервер отдаёт только транзакции за сегодня, и тогда deleteSyncedNotIn
    // вычистил бы всю остальную историю — поэтому тянем весь диапазон.
    private suspend fun fetchAccountTransactions(accountId: Long): List<TransactionResponseDto> =
        transactionRemote.getByAccountAndPeriod(accountId, PULL_START_DATE, PULL_END_DATE)

    private suspend fun upsertTransaction(tx: TransactionResponseDto) {
        val existing = transactionLocal.getByServerId(tx.id)
        if (existing == null) transactionLocal.insert(tx.toEntity())
        else transactionLocal.update(tx.toEntity(localId = existing.localId))
    }

    private suspend fun syncCategoriesFrom(remote: List<TransactionResponseDto>) {
        val categories = remote
            .map { it.category }
            .associate { it.id to CategoryEntity(it.id, it.name, it.emoji, it.isIncome) }
        if (categories.isNotEmpty()) categoryLocal.upsertAll(categories.values.toList())
    }

    private suspend fun pruneSyncedTransactions(serverIds: List<Long>) {
        if (serverIds.isEmpty()) transactionLocal.deleteAllSynced()
        else transactionLocal.deleteSyncedNotIn(serverIds)
    }

    private companion object {
        // Полный диапазон: тянем всю историю транзакций, а не только «сегодня».
        const val PULL_START_DATE = "1970-01-01"
        const val PULL_END_DATE = "2100-01-01"
    }
}
