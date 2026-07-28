package com.example.fianancetracker.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Composable
internal fun MainTab.icon(): ImageVector = when (this) {
    MainTab.EXPENSES -> ImageVector.vectorResource(R.drawable.expenses)
    MainTab.INCOMES -> Icons.AutoMirrored.Filled.TrendingUp
    MainTab.ACCOUNTS -> ImageVector.vectorResource(R.drawable.accounts)
}
