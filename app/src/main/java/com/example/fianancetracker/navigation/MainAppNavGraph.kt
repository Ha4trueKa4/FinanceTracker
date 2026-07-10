package com.example.fianancetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fianancetracker.ui.screens.AccountsScreen
import com.example.fianancetracker.ui.screens.ExpensesScreen
import com.example.fianancetracker.ui.screens.IncomeScreen
import com.example.fianancetracker.ui.screens.SplashScreen

@Composable
fun MainAppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Expenses.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Expenses.route) {
            ExpensesScreen()
        }
        composable(Screen.Income.route) {
            IncomeScreen()
        }
        composable(Screen.Accounts.route) {
            AccountsScreen()
        }
    }
}