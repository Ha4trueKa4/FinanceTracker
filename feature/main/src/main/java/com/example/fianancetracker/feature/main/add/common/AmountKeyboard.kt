package com.example.fianancetracker.feature.main.add.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val MAX_DIGITS = 12

fun appendKey(current: String, key: String): String {
    if (current.length >= MAX_DIGITS) return current
    return current + key
}

@Composable
internal fun AmountArea(amountText: String, isError: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = amountText,
                fontSize = 45.sp,
                lineHeight = 53.sp,
                fontWeight = FontWeight.Bold,
                color = if (isError) ErrorColor else AmountColor
            )
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 48.dp)
                    .background(CursorColor)
            )
        }
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 1.dp)
                .background(if (isError) ErrorColor else LineColor)
        )
    }
}
