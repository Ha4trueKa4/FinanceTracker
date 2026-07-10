package com.example.fianancetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")

    data object Expenses : Screen("expenses")
    data object Income : Screen("income")
    data object Accounts : Screen("accounts")

    companion object {
        data class BottomNavItem(
            val screen: Screen,
            val title: String,
            val icon: ImageVector
        )

        val bottomNavItems = listOf(
            BottomNavItem(Expenses, "Расходы", Icons.Filled.Receipt),
            BottomNavItem(Income, "Доходы", Icons.AutoMirrored.Filled.TrendingUp),
            BottomNavItem(Accounts, "Счета", Icons.Filled.Person),
        )
    }
}