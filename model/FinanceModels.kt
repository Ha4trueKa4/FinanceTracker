package com.example.fianancetracker.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ListItemModel(
    val id: String,
    val lead: Lead,
    val content: Content,
    val trail: Trail
) {
    data class Lead(
        val icon: ImageVector,
        val backgroundColor: Color
    )

    data class Content(
        val title: String,
        val subtitle: String = ""
    )

    data class Trail(
        val amount: String,
        val isPositive: Boolean = true
    )
}

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)

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

data class Income(
    val id: String,
    val title: String,
    val amount: Double,
    val category: IncomeCategory,
    val date: String,
    val description: String = "",
    val createdAt: String = ""
)

enum class IncomeCategory(val displayName: String) {
    SALARY("Зарплата"),
    FREELANCE("Фриланс"),
    INVESTMENTS("Инвестиции"),
    GIFTS("Подарки"),
    OTHER("Другое")
}

data class Account(
    val id: String,
    val name: String,
    val balance: Double,
    val cardNumber: String,
    val type: AccountType
)

enum class AccountType(val displayName: String) {
    DEBIT("Дебетовая"),
    CREDIT("Кредитная"),
    SAVINGS("Накопительная"),
    CASH("Наличные")
}