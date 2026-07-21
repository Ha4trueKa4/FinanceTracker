package com.example.fianancetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fianancetracker.presentation.screens.AccountsScreen
import com.example.fianancetracker.presentation.screens.AnalyticsScreen
import com.example.fianancetracker.presentation.screens.ExpensesScreen
import com.example.fianancetracker.presentation.screens.IncomeScreen
import com.example.fianancetracker.presentation.screens.SplashScreen
import com.example.fianancetracker.presentation.viewmodel.AccountsViewModel
import com.example.fianancetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.fianancetracker.presentation.viewmodel.ExpensesViewModel
import com.example.fianancetracker.presentation.viewmodel.IncomeViewModel
import org.koin.androidx.compose.koinViewModel

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
            val viewModel: ExpensesViewModel = koinViewModel()
            ExpensesScreen(
                viewModel = viewModel
            )
        }
        composable(Screen.Incomes.route) {
            val viewModel: IncomeViewModel = koinViewModel()
            IncomeScreen(
                viewModel = viewModel
            )
        }
        composable(Screen.Accounts.route) {
            val viewModel: AccountsViewModel = koinViewModel()
            AccountsScreen(
                viewModel = viewModel
            )
        }
        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = koinViewModel()
            AnalyticsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}