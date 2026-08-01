package com.example.fianancetracker.core.data.network.dto

data class AccountDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val emoji: String,
    val balance: String,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)

data class AccountRequestDto(
    val name: String,
    val emoji: String,
    val balance: String,
    val currency: String
)

data class AccountBriefDto(
    val id: Long,
    val name: String,
    val emoji: String,
    val balance: String,
    val currency: String
)
