package com.example.fianancetracker.feature.analytics.components.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

fun formatDateRussian(date: LocalDate): String =
    "${date.dayOfMonth} ${russianMonths[date.monthValue - 1]}. ${date.year}"

/** Поле-плашка с выбранной датой (или плейсхолдером) для диапазона дат. */
@Composable
fun DateField(
    date: LocalDate?,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFC4C7C5)),
        color = Color.Transparent,
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(
                text = if (date != null) formatDateRussian(date) else placeholder,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1D1B20),
            )
        }
    }
}
