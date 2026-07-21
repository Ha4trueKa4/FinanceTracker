package com.example.fianancetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fianancetracker.presentation.screens.MainScreen
import com.example.fianancetracker.presentation.theme.FiananceTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiananceTrackerTheme {
                MainScreen()
            }
        }
    }
}