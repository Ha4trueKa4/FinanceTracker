package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fianancetracker.presentation.navigation.MainAppNavGraph
import com.example.fianancetracker.presentation.navigation.Screen
import com.example.fianancetracker.presentation.components.FinanceFloatingActionButton
import com.example.fianancetracker.presentation.components.FinanceTopBar


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentDestination?.route != Screen.Splash.route) {
                Box {
                    NavigationBar {
                        Screen.bottomNavItems().forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == item.screen.route
                            } == true

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) }
                            )
                        }
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        },
        floatingActionButton = { FinanceFloatingActionButton({})},
        topBar = { FinanceTopBar {  } }
    ) { innerPadding ->
        MainAppNavGraph(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}