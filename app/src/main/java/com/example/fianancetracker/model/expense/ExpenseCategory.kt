package com.example.fianancetracker.model.expense

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