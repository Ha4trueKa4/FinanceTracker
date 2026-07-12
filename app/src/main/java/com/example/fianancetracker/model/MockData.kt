package com.example.fianancetracker.model

import com.example.fianancetracker.model.account.Account
import com.example.fianancetracker.model.account.AccountType
import com.example.fianancetracker.model.expense.Expense
import com.example.fianancetracker.model.expense.ExpenseCategory
import com.example.fianancetracker.model.income.Income
import com.example.fianancetracker.model.income.IncomeCategory

object MockData {

    val expenseItems = listOf(
        Expense("1", "Кофе в старбакс", 450.0, ExpenseCategory.FOOD, "10.07.2026"),
        Expense("2", "Обед в кафе", 850.0, ExpenseCategory.FOOD, "10.07.2026"),
        Expense("3", "Такси до работы", 600.0, ExpenseCategory.TRANSPORT, "09.07.2026"),
        Expense("4", "Продукты в магазине", 3200.0, ExpenseCategory.FOOD, "09.07.2026"),
        Expense("5", "Кино с друзьями", 1200.0, ExpenseCategory.ENTERTAINMENT, "08.07.2026"),
        Expense("6", "Аптека", 890.0, ExpenseCategory.HEALTH, "08.07.2026"),
        Expense("7", "Коммунальные платежи", 4500.0, ExpenseCategory.HOUSING, "07.07.2026"),
        Expense("8", "Курсы программирования", 5000.0, ExpenseCategory.EDUCATION, "07.07.2026"),
        Expense("9", "Новая футболка", 2500.0, ExpenseCategory.SHOPPING, "06.07.2026"),
        Expense("10", "Ужин в ресторане", 3500.0, ExpenseCategory.FOOD, "06.07.2026"),
        Expense("11", "Метро", 150.0, ExpenseCategory.TRANSPORT, "05.07.2026"),
        Expense("12", "Подписка Spotify", 500.0, ExpenseCategory.ENTERTAINMENT, "04.07.2026"),
    )

    val incomeItems = listOf(
        Income("1", "Продажа старой мебели", 8500.0, IncomeCategory.OTHER, "10.07.2026"),
        Income("2", "Возврат налога", 15000.0, IncomeCategory.OTHER, "09.07.2026"),
        Income("3", "Премия за проект", 25000.0, IncomeCategory.SALARY, "08.07.2026"),
        Income("4", "Подработка фриланс", 13450.0, IncomeCategory.FREELANCE, "07.07.2026"),
        Income("5", "Сдача квартиры", 38000.0, IncomeCategory.OTHER, "06.07.2026"),
        Income("6", "Кэшбэк на карту", 1200.0, IncomeCategory.OTHER, "05.07.2026"),
        Income("7", "Подарок от родителей", 5000.0, IncomeCategory.GIFTS, "04.07.2026"),
    )

    val accountItems = listOf(
        Account("1", "Основной счёт", 45230.0, "•••• 4582", AccountType.DEBIT),
        Account("2", "Накопительный", 120000.0, "•••• 9012", AccountType.SAVINGS),
        Account("3", "Кредитная карта", -15000.0, "•••• 3478", AccountType.CREDIT),
        Account("4", "Наличные", 8500.0, "", AccountType.CASH),
    )

    fun getExpenseListItems(): List<ListItemModel> {
        val expenseEmojis = mapOf(
            "Кофе в старбакс" to "☕",
            "Обед в кафе" to "🍕",
            "Такси до работы" to "🚕",
            "Продукты в магазине" to "🛒",
            "Кино с друзьями" to "🎬",
            "Аптека" to "💊",
            "Коммунальные платежи" to "💡",
            "Курсы программирования" to "💻",
            "Новая футболка" to "👕",
            "Ужин в ресторане" to "🍽️",
            "Метро" to "🚇",
            "Подписка Spotify" to "🎵",
        )
        return expenseItems.map { expense ->
            ListItemModel(
                id = expense.id,
                lead = ListItemModel.Lead(
                    emoji = expenseEmojis[expense.title] ?: "💸"
                ),
                content = ListItemModel.Content(
                    title = expense.title,
                    subtitle = "${expense.category.displayName} • ${expense.date}"
                ),
                trail = ListItemModel.Trail(
                    amount = String.format("%,.0f ₽", expense.amount),
                    isPositive = false
                )
            )
        }
    }

    fun getIncomeListItems(): List<ListItemModel> {
        val incomeEmojis = mapOf(
            "Продажа старой мебели" to "🛋",
            "Возврат налога" to "📋",
            "Премия за проект" to "💼",
            "Подработка фриланс" to "💻",
            "Сдача квартиры" to "🏠",
            "Кэшбэк на карту" to "💳",
            "Подарок от родителей" to "🎁",
        )
        return incomeItems.map { income ->
            ListItemModel(
                id = income.id,
                lead = ListItemModel.Lead(
                    emoji = incomeEmojis[income.title] ?: "💰"
                ),
                content = ListItemModel.Content(
                    title = income.title,
                    subtitle = "${income.category.displayName} • ${income.date}"
                ),
                trail = ListItemModel.Trail(
                    amount = String.format("%,.0f ₽", income.amount),
                    isPositive = true
                )
            )
        }
    }

    fun getAccountListItems(): List<ListItemModel> {
        val accountEmojis = mapOf(
            AccountType.DEBIT to "🏦",
            AccountType.SAVINGS to "🐖",
            AccountType.CREDIT to "💳",
            AccountType.CASH to "💵",
        )
        return accountItems.map { account ->
            ListItemModel(
                id = account.id,
                lead = ListItemModel.Lead(
                    emoji = accountEmojis[account.type] ?: "💰"
                ),
                content = ListItemModel.Content(
                    title = account.name,
                    subtitle = account.cardNumber.ifEmpty { account.type.displayName }
                ),
                trail = ListItemModel.Trail(
                    amount = String.format("%,.0f ₽", account.balance),
                    isPositive = account.balance >= 0
                )
            )
        }
    }

    val totalExpenses: Double = expenseItems.sumOf { it.amount }
    val totalIncome: Double = incomeItems.sumOf { it.amount }
    val totalBalance: Double = accountItems.sumOf { it.balance }
}