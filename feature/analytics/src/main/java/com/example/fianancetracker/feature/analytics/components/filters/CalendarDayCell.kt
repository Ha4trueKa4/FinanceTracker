package com.example.fianancetracker.feature.analytics.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

private val RangeColor = Color(0xFFE7E3F2)
private val SelectedColor = Color(0xFF6750A4)

/** Ячейка одного дня в календаре: подсветка диапазона и кружок для его границ. */
@Composable
internal fun CalendarDayCell(
    date: LocalDate,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRangeStart = date == startDate
    val isRangeEnd = date == endDate
    val isInRange = startDate != null && endDate != null &&
        !date.isBefore(startDate) && !date.isAfter(endDate) && !isRangeStart && !isRangeEnd

    val rangeModifier = when {
        isInRange -> Modifier.background(RangeColor)
        isRangeStart && endDate != null && !isRangeEnd -> Modifier.drawBehind {
            drawRect(RangeColor, Offset(size.width / 2, 0f), Size(size.width / 2, size.height))
        }
        isRangeEnd && startDate != null && !isRangeStart -> Modifier.drawBehind {
            drawRect(RangeColor, Offset(0f, 0f), Size(size.width / 2, size.height))
        }
        else -> Modifier
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .then(rangeModifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (isRangeStart || isRangeEnd) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SelectedColor),
                contentAlignment = Alignment.Center,
            ) {
                DayNumber(date.dayOfMonth, Color.White)
            }
        } else {
            DayNumber(date.dayOfMonth, Color(0xFF1D1B20))
        }
    }
}

@Composable
private fun DayNumber(day: Int, color: Color) {
    Text(
        text = day.toString(),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = color,
        textAlign = TextAlign.Center,
    )
}
