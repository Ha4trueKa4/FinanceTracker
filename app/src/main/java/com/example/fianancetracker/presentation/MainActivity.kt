package com.example.fianancetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.fianancetracker.core.ui.theme.FiananceTrackerTheme
import com.example.fianancetracker.presentation.navigation.MainAppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FiananceTrackerTheme {
                val navController = rememberNavController()
                MainAppNavGraph(navController = navController)
            }
        }
    }
}
