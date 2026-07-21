package com.example.fianancetracker.data.repository

import com.example.fianancetracker.data.mapper.toDomain
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.domain.account.Account
import com.example.fianancetracker.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val api: FinanceApi
) : AccountRepository {

    override suspend fun getAccounts(): List<Account> {
        val response = api.getAccounts()
        if (!response.isSuccessful) {
            throw Exception("Ошибка загрузки счетов: ${response.code()}")
        }
        return response.body()?.map { it.toDomain() } ?: emptyList()
    }
}