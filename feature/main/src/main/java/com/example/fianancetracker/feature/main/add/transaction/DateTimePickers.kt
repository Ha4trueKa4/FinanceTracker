package com.example.fianancetracker.feature.main.add.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.fianancetracker.feature.main.add.common.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogM3(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = dateState.selectedDateMillis
                if (millis != null) {
                    onConfirm(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                } else {
                    onDismiss()
                }
            }) { Text("Применить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
    ) {
        DatePicker(state = dateState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSheet(
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hour by remember { mutableIntStateOf(initial.hour) }
    var minute by remember { mutableIntStateOf(initial.minute) }
    var active by remember { mutableStateOf(TimeSelection.HOUR) }
    var partial by remember { mutableIntStateOf(0) }

    fun onDigit(key: String) {
        val digit = key.toIntOrNull() ?: return
        if (active == TimeSelection.HOUR) {
            val (value, next, complete) = typeDigit(hour, partial, digit, max = 23)
            hour = value
            if (complete) {
                active = TimeSelection.MINUTE
                partial = 0
            } else {
                partial = next
            }
        } else {
            val (value, next, _) = typeDigit(minute, partial, digit, max = 59)
            minute = value
            partial = next
        }
    }

    fun onBackspace() {
        if (active == TimeSelection.HOUR) {
            hour /= 10
            partial = (partial - 1).coerceAtLeast(0)
        } else if (partial == 0 && minute == 0) {
            active = TimeSelection.HOUR
            partial = 0
        } else {
            minute /= 10
            partial = (partial - 1).coerceAtLeast(0)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color(0xFFECE6F0),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Выберите время",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF49454F),
                modifier = Modifier.padding(start = 24.dp, top = 24.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.Top,
            ) {
                TimeField(
                    value = hour,
                    label = "Часы",
                    isActive = active == TimeSelection.HOUR,
                    onClick = { active = TimeSelection.HOUR; partial = 0 },
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(72.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = ":",
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextColor,
                    )
                }
                TimeField(
                    value = minute,
                    label = "Минуты",
                    isActive = active == TimeSelection.MINUTE,
                    onClick = { active = TimeSelection.MINUTE; partial = 0 },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отменить", color = TimeActiveBorder, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onConfirm(LocalTime.of(hour, minute)) }) {
                    Text("Применить", color = TimeActiveBorder, fontWeight = FontWeight.Medium)
                }
            }

            NumericKeyboard(
                onKeyClick = ::onDigit,
                onBackspace = ::onBackspace,
                backgroundColor = Color(0xFFECE6F0),
            )
        }
    }
}

@Composable
internal fun TimeField(
    value: Int,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (isActive) TimeActiveBg else TimeInactiveBg
    val textColor = if (isActive) TimeActiveText else TextColor
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bg)
                .then(
                    if (isActive) Modifier.border(2.dp, TimeActiveBorder, RoundedCornerShape(8.dp))
                    else Modifier
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%02d".format(value),
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Normal,
                    color = textColor,
                )
                if (isActive) {
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(42.dp)
                            .background(TimeActiveBorder),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF49454F),
        )
    }
}

internal enum class TimeSelection { HOUR, MINUTE }
