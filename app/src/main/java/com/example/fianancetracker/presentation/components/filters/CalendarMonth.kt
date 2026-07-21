package com.example.fianancetracker.presentation.components.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.time.YearMonth
import kotlin.math.abs

val russianMonths = listOf(
    "янв", "фев", "мар", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)

val russianMonthsFull = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
)

val dayOfWeekHeaders = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

fun formatDateRussian(date: LocalDate): String {
    return "${date.dayOfMonth} ${russianMonths[date.monthValue - 1]}. ${date.year}"
}

@Composable
fun CalendarMonth(
    yearMonth: YearMonth,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    showNavigation: Boolean = false,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    val year = yearMonth.year
    val month = yearMonth.monthValue
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = LocalDate.of(year, month, 1).dayOfWeek.value

    val calendarDays = remember(year, month) {
        val result = mutableListOf<Int?>()
        repeat(firstDayOfWeek - 1) { result.add(null) }
        for (day in 1..daysInMonth) {
            result.add(day)
        }
        while (result.size % 7 != 0) {
            result.add(null)
        }
        result
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showNavigation) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Предыдущий",
                        tint = Color(0xFF49454F)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
            Text(
                text = "${russianMonthsFull[month - 1]} $year",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1B20)
            )
            if (showNavigation) {
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Следующий",
                        tint = Color(0xFF49454F)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            dayOfWeekHeaders.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF49454F),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            calendarDays.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        if (day != null) {
                            val date = LocalDate.of(year, month, day)
                            val isRangeStart = date == startDate
                            val isRangeEnd = date == endDate
                            val isInRange = startDate != null && endDate != null &&
                                    !date.isBefore(startDate) && !date.isAfter(endDate) &&
                                    !isRangeStart && !isRangeEnd

                            val rangeModifier = when {
                                isInRange -> Modifier.background(Color(0xFFE7E3F2))
                                isRangeStart && endDate != null && !isRangeEnd -> Modifier.drawBehind {
                                    drawRect(
                                        color = Color(0xFFE7E3F2),
                                        topLeft = Offset(size.width / 2, 0f),
                                        size = Size(size.width / 2, size.height)
                                    )
                                }
                                isRangeEnd && startDate != null && !isRangeStart -> Modifier.drawBehind {
                                    drawRect(
                                        color = Color(0xFFE7E3F2),
                                        topLeft = Offset(0f, 0f),
                                        size = Size(size.width / 2, size.height)
                                    )
                                }
                                else -> Modifier
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .then(rangeModifier)
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRangeStart || isRangeEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6750A4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF1D1B20),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateField(
    date: LocalDate?,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFC4C7C5)),
        color = Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = if (date != null) formatDateRussian(date) else placeholder,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1D1B20)
            )
        }
    }
}