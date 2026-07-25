package com.example.fianancetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fianancetracker.feature.analytics.AnalyticsScreen
import com.example.fianancetracker.feature.analytics.AnalyticsViewModel
import com.example.fianancetracker.feature.main.MainScreen
import com.example.fianancetracker.presentation.screens.SplashScreen
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
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
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
