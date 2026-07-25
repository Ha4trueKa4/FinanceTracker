package com.example.fianancetracker.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun FinanceFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFFEADDFF),
        contentColor = Color(0xFF1D1B20),
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Добавить",
            modifier = Modifier.size(24.dp)
        )
    }
}