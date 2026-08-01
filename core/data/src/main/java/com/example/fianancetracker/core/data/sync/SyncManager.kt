package com.example.fianancetracker.core.data.sync

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.db.dao.AccountDao
import com.example.fianancetracker.core.data.db.dao.CategoryDao
import com.example.fianancetracker.core.data.db.dao.PendingSyncDao
import com.example.fianancetracker.core.data.db.dao.TransactionDao
import com.example.fianancetracker.core.data.db.entity.CategoryEntity
import com.example.fianancetracker.core.data.db.entity.PendingType
import com.example.fianancetracker.core.data.db.mapper.toEntity
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto
import com.example.fianancetracker.core.data.network.dto.TransactionRequestDto
import com.example.fianancetracker.core.domain.common.AppError
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncManager(
    private val api: FinanceApi,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val pendingSyncDao: PendingSyncDao,
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
        val ops = pendingSyncDao.getAll()
        for (op in ops) {
            val result = runOp(op.type, op.targetId, op.payload)
            val error = result.exceptionOrNull()
            when {
                // Успех — операция доехала, убираем из очереди.
                result.isSuccess -> pendingSyncDao.deleteById(op.localId)
                // Постоянная клиентская ошибка (4xx): операция невалидна, повтор не поможет —
                // выкидываем, иначе она навсегда заблокирует очередь.
                error is AppError.Server && error.code in 400..499 ->
                    pendingSyncDao.deleteById(op.localId)
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
                val created = api.createTransaction(dto).bodyOrThrow()
                transactionDao.markSynced(targetId, created.id)
                Unit
            }

            PendingType.UPDATE_TRANSACTION.name -> {
                val serverId = transactionDao.getByLocalId(targetId)?.serverId
                if (serverId == null) {
                    Result.failure(AppError.NoInternet)
                } else apiCall {
                    val dto = resolveTxAccount(targetId, payload)
                    api.updateTransaction(serverId, dto).bodyOrThrow()
                    transactionDao.getByLocalId(targetId)?.let {
                        transactionDao.update(it.copy(synced = true))
                    }
                    Unit
                }
            }

            PendingType.CREATE_ACCOUNT.name -> apiCall {
                val dto = gson.fromJson(payload, AccountRequestDto::class.java)
                val created = api.createAccount(dto).bodyOrThrow()
                // Сверка: временный локальный id → серверный (счёт, ссылки транзакций, очередь).
                accountDao.reassignId(oldId = targetId, newId = created.id)
                transactionDao.reassignAccount(oldAccountId = targetId, newAccountId = created.id)
                pendingSyncDao.retargetAccountOps(oldId = targetId, newId = created.id)
                Unit
            }

            PendingType.UPDATE_ACCOUNT.name -> {
                val serverId = accountDao.getById(targetId)?.serverId
                if (serverId == null) {
                    // Счёт ещё не создан на сервере — оставляем операцию в очереди до следующего цикла.
                    Result.failure(AppError.Unknown("Счёт ещё не синхронизирован"))
                } else apiCall {
                    val dto = gson.fromJson(payload, AccountRequestDto::class.java)
                    api.updateAccount(serverId, dto).bodyOrThrow()
                    accountDao.markSynced(targetId)
                    Unit
                }
            }

            else -> Result.success(Unit)
        }

    // В payload транзакции id счёта мог быть временным (оффлайн-счёт). Берём актуальный
    // id из сущности транзакции — после сверки счёта он уже указывает на серверный.
    private suspend fun resolveTxAccount(txLocalId: Long, payload: String): TransactionRequestDto {
        val dto = gson.fromJson(payload, TransactionRequestDto::class.java)
        val accountId = transactionDao.getByLocalId(txLocalId)?.accountId ?: dto.accountId
        if (accountId <= 0) throw AppError.Unknown("Счёт транзакции ещё не синхронизирован")
        return dto.copy(accountId = accountId)
    }

    // Не затираем локальные несинхронизированные счета (оффлайн-создание/правку) данными сервера.
    private suspend fun upsertServerAccounts(remote: List<AccountDto>) {
        val localById = accountDao.getAll().associateBy { it.id }
        val toUpsert = remote.mapNotNull { dto ->
            val local = localById[dto.id]
            if (local != null && !local.synced) null else dto.toEntity()
        }
        if (toUpsert.isNotEmpty()) accountDao.upsertAll(toUpsert)
    }

    suspend fun pullAccounts(): Result<Unit> = apiCall {
        upsertServerAccounts(api.getAccounts().bodyOrThrow())
        Unit
    }

    suspend fun pullCategories(): Result<Unit> = apiCall {
        val categories = api.getCategories().bodyOrThrow().map { it.toEntity() }
        categoryDao.upsertAll(categories)
        Unit
    }

    suspend fun pullTransactions(): Result<Unit> = apiCall {
        val accounts = api.getAccounts().bodyOrThrow()
        upsertServerAccounts(accounts)

        val categories = mutableMapOf<Long, CategoryEntity>()
        val serverIds = mutableListOf<Long>()
        for (account in accounts) {
            // Без явного периода сервер отдаёт только транзакции за сегодня, и тогда
            // deleteSyncedNotIn ниже вычистит всю остальную историю. Тянем весь диапазон.
            val transactions = api
                .getTransactionsByAccountAndPeriod(account.id, PULL_START_DATE, PULL_END_DATE)
                .body().orEmpty()
            for (tx in transactions) {
                serverIds += tx.id
                categories[tx.category.id] = CategoryEntity(
                    id = tx.category.id,
                    name = tx.category.name,
                    emoji = tx.category.emoji,
                    isIncome = tx.category.isIncome,
                )
                val existing = transactionDao.getByServerId(tx.id)
                if (existing == null) {
                    transactionDao.insert(tx.toEntity())
                } else {
                    transactionDao.update(tx.toEntity(localId = existing.localId))
                }
            }
        }

        if (categories.isNotEmpty()) categoryDao.upsertAll(categories.values.toList())
        if (serverIds.isEmpty()) transactionDao.deleteAllSynced()
        else transactionDao.deleteSyncedNotIn(serverIds)
        Unit
    }

    private companion object {
        // Полный диапазон: тянем всю историю транзакций, а не только «сегодня».
        const val PULL_START_DATE = "1970-01-01"
        const val PULL_END_DATE = "2100-01-01"
    }
}
