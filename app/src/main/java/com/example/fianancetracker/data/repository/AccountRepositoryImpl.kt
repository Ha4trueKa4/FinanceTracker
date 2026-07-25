package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.common.apiCall
import com.example.fianancetracker.data.common.bodyOrThrow
import com.example.fianancetracker.data.mapper.toDomain
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val api: FinanceApi
) : AccountRepository {

    override suspend fun getAccounts(): Result<List<Account>> = apiCall {
        api.getAccounts().bodyOrThrow().map { it.toDomain() }
    }
}