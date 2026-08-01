package com.example.fianancetracker.core.data.network.api

import com.example.fianancetracker.core.data.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {

    @GET("accounts")
    suspend fun getAccounts(): Response<List<AccountDto>>

    @POST("accounts")
    suspend fun createAccount(
        @Body body: AccountRequestDto
    ): Response<AccountDto>

    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: Long,
        @Body body: AccountRequestDto
    ): Response<AccountDto>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsByAccountAndPeriod(
        @Path("accountId") accountId: Long,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<TransactionResponseDto>>

    @GET("transactions/{id}")
    suspend fun getTransactionById(
        @Path("id") id: Long
    ): Response<TransactionResponseDto>

    @POST("transactions")
    suspend fun createTransaction(
        @Body body: TransactionRequestDto
    ): Response<TransactionDto>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Long,
        @Body body: TransactionRequestDto
    ): Response<TransactionResponseDto>
}
