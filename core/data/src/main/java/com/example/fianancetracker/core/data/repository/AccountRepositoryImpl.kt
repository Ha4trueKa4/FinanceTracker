package com.example.fianancetracker.core.data.repository

import com.example.fianancetracker.core.data.common.apiCall
import com.example.fianancetracker.core.data.common.bodyOrThrow
import com.example.fianancetracker.core.data.mapper.toDomain
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val api: FinanceApi
) : AccountRepository {

    override suspend fun getAccounts(): Result<List<Account>> = apiCall {
        api.getAccounts().bodyOrThrow().map { it.toDomain() }
    }
}