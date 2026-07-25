package com.example.fianancetracker.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object Analytics : Screen("analytics")
}
