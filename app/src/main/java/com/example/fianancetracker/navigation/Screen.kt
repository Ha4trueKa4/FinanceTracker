package com.example.fianancetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.fianancetracker.R

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")

    data object Expenses : Screen("expenses")
    data object Incomes : Screen("income")
    data object Accounts : Screen("accounts")

    companion object {
        data class BottomNavItem(
            val screen: Screen,
            val title: String,
            val icon: ImageVector
        )

        @Composable
        fun bottomNavItems(): List<BottomNavItem> = listOf(
            BottomNavItem(Expenses, "Расходы", ImageVector.vectorResource(R.drawable.expenses)),
            BottomNavItem(Incomes, "Доходы", Icons.AutoMirrored.Filled.TrendingUp),
            BottomNavItem(Accounts, "Счета", ImageVector.vectorResource(R.drawable.accounts)),
        )
    }
}