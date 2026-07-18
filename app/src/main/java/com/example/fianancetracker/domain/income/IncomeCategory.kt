package com.example.fianancetracker.domain.income

enum class IncomeCategory(val displayName: String) {
    SALARY("Зарплата"),
    FREELANCE("Фриланс"),
    INVESTMENTS("Инвестиции"),
    GIFTS("Подарки"),
    OTHER("Другое")
}