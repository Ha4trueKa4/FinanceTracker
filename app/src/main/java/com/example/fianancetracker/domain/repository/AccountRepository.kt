package com.example.fianancetracker.domain.repository

import com.example.fianancetracker.domain.account.Account

interface AccountRepository {
    suspend fun getAccounts(): List<Account>
}