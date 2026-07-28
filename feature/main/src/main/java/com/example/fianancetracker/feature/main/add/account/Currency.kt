package com.example.fianancetracker.feature.main.add.account

enum class Currency(val code: String, val symbol: String, val title: String) {
    RUB("RUB", "₽", "Российский рубль"),
    USD("USD", "$", "Доллар США"),
    EUR("EUR", "€", "Евро"),
}
