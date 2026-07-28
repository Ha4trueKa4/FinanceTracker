package com.example.fianancetracker.feature.main.add.common

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
internal fun RowSeparator() {
    HorizontalDivider(thickness = 0.5.dp, color = DividerColor.copy(alpha = 0.4f))
}
