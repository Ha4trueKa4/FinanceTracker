package com.example.fianancetracker.feature.analytics.components.bottom_sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.feature.analytics.components.filters.CalendarMonth
import com.example.fianancetracker.feature.analytics.components.filters.DateField
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangeSheet(
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onApply: (LocalDate?, LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }
    var displayMonth by remember {
        mutableStateOf(
            (initialStartDate ?: LocalDate.now()).withDayOfMonth(1)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF79747E).copy(alpha = 0.4f))
                )
            }

            Text(
                text = "Произвольный период",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateField(
                    date = startDate,
                    placeholder = "Начало",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(1.dp)
                        .background(Color(0xFFC4C7C5))
                )
                DateField(
                    date = endDate,
                    placeholder = "Конец",
                    modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CalendarMonth(
                    yearMonth = YearMonth.from(displayMonth),
                    startDate = startDate,
                    endDate = endDate,
                    showNavigation = true,
                    onPreviousMonth = { displayMonth = displayMonth.minusMonths(1) },
                    onNextMonth = { displayMonth = displayMonth.plusMonths(1) },
                    onDateSelected = { date ->
                        when {
                            startDate == null -> startDate = date
                            endDate == null && date >= startDate!! -> endDate = date
                            else -> {
                                if (endDate == null) {
                                    startDate = date
                                } else {
                                    val distToStart =
                                        abs(date.toEpochDay() - startDate!!.toEpochDay())
                                    val distToEnd = abs(date.toEpochDay() - endDate!!.toEpochDay())
                                    if (distToStart <= distToEnd) {
                                        startDate = date
                                    } else {
                                        endDate = date
                                    }
                                    if (startDate!! > endDate!!) {
                                        val temp = startDate
                                        startDate = endDate
                                        endDate = temp
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.clickable(onClick = onDismiss),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "Отмена",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6750A4),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    modifier = Modifier.clickable { onApply(startDate, endDate) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF6750A4)
                ) {
                    Text(
                        text = "Применить",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
