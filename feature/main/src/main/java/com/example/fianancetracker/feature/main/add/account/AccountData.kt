package com.example.fianancetracker.feature.main.add.account

import androidx.annotation.StringRes
import com.example.fianancetracker.feature.main.R

enum class Currency(val code: String, val symbol: String, @StringRes val titleRes: Int) {
    RUB("RUB", "₽", R.string.currency_rub),
    USD("USD", "$", R.string.currency_usd),
    EUR("EUR", "€", R.string.currency_eur),
}

internal val ACCOUNT_EMOJIS = listOf("🏦", "💳", "🐖", "💵", "💰", "🪙", "🏧", "💶")
