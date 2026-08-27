package com.example.fianancetracker.core.data.datasource.remote

import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.TransactionDto
import com.example.fianancetracker.core.data.network.dto.TransactionRequestDto
import com.example.fianancetracker.core.data.network.dto.TransactionResponseDto

/** Удалённый источник данных по транзакциям: тонкая обёртка над [FinanceApi]. */
internal interface TransactionRemoteDataSource {
    suspend fun getById(id: Long): TransactionResponseDto
    suspend fun getByAccountAndPeriod(
        accountId: Long,
        startDate: String?,
        endDate: String?,
    ): List<TransactionResponseDto>
    suspend fun create(body: TransactionRequestDto): TransactionDto
    suspend fun update(id: Long, body: TransactionRequestDto): TransactionResponseDto
}

internal class TransactionRemoteDataSourceImpl(private val api: FinanceApi) : TransactionRemoteDataSource {
    override suspend fun getById(id: Long): TransactionResponseDto =
        api.getTransactionById(id).bodyOrThrow()

    override suspend fun getByAccountAndPeriod(
        accountId: Long,
        startDate: String?,
        endDate: String?,
    ): List<TransactionResponseDto> =
        api.getTransactionsByAccountAndPeriod(accountId, startDate, endDate).body().orEmpty()

    override suspend fun create(body: TransactionRequestDto): TransactionDto =
        api.createTransaction(body).bodyOrThrow()

    override suspend fun update(id: Long, body: TransactionRequestDto): TransactionResponseDto =
        api.updateTransaction(id, body).bodyOrThrow()
}
