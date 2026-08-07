package com.example.fianancetracker.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fianancetracker.feature.analytics.AnalyticsScreen
import com.example.fianancetracker.feature.analytics.AnalyticsViewModel
import com.example.fianancetracker.feature.main.MainScreen
import com.example.fianancetracker.feature.settings.SettingsSheet
import com.example.fianancetracker.feature.settings.pin.PinMode
import com.example.fianancetracker.feature.settings.pin.PinScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainAppNavGraph(
    navController: NavHostController,
    startAtPin: Boolean = false,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (startAtPin) Screen.Pin.route else Screen.Main.route,
        modifier = modifier
    ) {
        composable(Screen.Pin.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.BottomCenter,
            ) {
                PinScreen(
                    mode = PinMode.VERIFY,
                    showHandle = false,
                    onSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Pin.route) { inclusive = true }
                        }
                    },
                )
            }
        }
        composable(Screen.Main.route) {
            var showSettings by rememberSaveable { mutableStateOf(false) }
            MainScreen(
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onOpenSettings = { showSettings = true },
            )
            if (showSettings) {
                SettingsSheet(onDismiss = { showSettings = false })
            }
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
