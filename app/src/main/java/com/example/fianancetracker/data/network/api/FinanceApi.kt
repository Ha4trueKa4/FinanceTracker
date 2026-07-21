package com.example.fianancetracker.data.network.api

import com.example.fianancetracker.data.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {

    @GET("accounts")
    suspend fun getAccounts(): Response<List<AccountDto>>

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsByAccountAndPeriod(
        @Path("accountId") accountId: Long,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<TransactionResponseDto>>
}