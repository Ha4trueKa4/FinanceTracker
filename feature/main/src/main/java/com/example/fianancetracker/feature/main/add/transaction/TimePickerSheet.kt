package com.example.fianancetracker.feature.main.add.transaction
import com.example.fianancetracker.feature.main.add.common.*

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime

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
