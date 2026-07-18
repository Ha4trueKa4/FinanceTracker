package com.example.fianancetracker.domain.expense

enum class ExpenseCategory(val displayName: String) {
    FOOD("Еда"),
    TRANSPORT("Транспорт"),
    SHOPPING("Покупки"),
    ENTERTAINMENT("Развлечения"),
    HEALTH("Здоровье"),
    HOUSING("Жильё"),
    EDUCATION("Образование"),
    OTHER("Другое")
}