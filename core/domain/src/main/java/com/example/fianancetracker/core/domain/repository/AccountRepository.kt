package com.example.fianancetracker.core.domain.repository

import com.example.fianancetracker.core.domain.account.Account

interface AccountRepository {
    suspend fun getAccounts(): Result<List<Account>>
}