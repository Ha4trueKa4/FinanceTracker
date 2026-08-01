package com.example.fianancetracker.core.data.datasource.remote

import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.dto.AccountDto
import com.example.fianancetracker.core.data.network.dto.AccountRequestDto

/** Удалённый источник данных по счетам: тонкая обёртка над [FinanceApi], скрывающая Retrofit-Response. */
interface AccountRemoteDataSource {
    suspend fun getAccounts(): List<AccountDto>
    suspend fun createAccount(body: AccountRequestDto): AccountDto
    suspend fun updateAccount(id: Long, body: AccountRequestDto): AccountDto
}

class AccountRemoteDataSourceImpl(private val api: FinanceApi) : AccountRemoteDataSource {
    override suspend fun getAccounts(): List<AccountDto> = api.getAccounts().bodyOrThrow()

    override suspend fun createAccount(body: AccountRequestDto): AccountDto =
        api.createAccount(body).bodyOrThrow()

    override suspend fun updateAccount(id: Long, body: AccountRequestDto): AccountDto =
        api.updateAccount(id, body).bodyOrThrow()
}
