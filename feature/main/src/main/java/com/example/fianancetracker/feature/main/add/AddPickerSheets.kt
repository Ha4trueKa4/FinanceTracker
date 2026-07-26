package com.example.fianancetracker.feature.main.add

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.ui.utils.accountEmoji
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

private val DividerColor = Color(0xFFC4C7C5)
private val TextColor = Color(0xFF1D1B20)
private val AccentColor = Color(0xFF7B5EA7)

/** Пикер выбора статьи. Одиночный выбор: тап по строке выбирает и закрывает лист. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = "Статья", onDismiss = onDismiss) {
        categories.forEachIndexed { index, category ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = category.emoji,
                name = category.name,
                isSelected = category.id == selectedId,
                onClick = { onSelect(category.id) },
            )
        }
    }
}

/** Пикер выбора счёта. Одиночный выбор: тап по строке выбирает и закрывает лист. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    accounts: List<Account>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = "Счёт", onDismiss = onDismiss) {
        accounts.forEachIndexed { index, account ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = accountEmoji(account.type),
                name = account.name,
                subtitle = account.type.displayName,
                isSelected = account.id == selectedId,
                onClick = { onSelect(account.id) },
            )
        }
    }
}

private val TimeActiveBg = Color(0xFFEADDFF)
private val TimeActiveBorder = Color(0xFF6750A4)
private val TimeActiveText = Color(0xFF4F378A)
private val TimeInactiveBg = Color(0xFFE6E0E9)

private enum class TimeField { HOUR, MINUTE }

/**
 * Пикер времени в bottom sheet над клавиатурой (не отдельный диалог). Сверху — боксы часы:минуты
 * в стиле M3 TimeInput / Figma, снизу — наша цифровая клавиатура (та же, что на форме добавления):
 * она печатает в активный бокс, системная клавиатура не поднимается.
 */
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
    var active by remember { mutableStateOf(TimeField.HOUR) }
    // Сколько цифр уже введено в активный бокс с момента фокуса (0..2) — для до-набора и авто-перехода.
    var partial by remember { mutableIntStateOf(0) }

    fun onDigit(key: String) {
        val digit = key.toIntOrNull() ?: return
        if (active == TimeField.HOUR) {
            val (value, next, complete) = typeDigit(hour, partial, digit, max = 23)
            hour = value
            if (complete) {
                active = TimeField.MINUTE
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
        if (active == TimeField.HOUR) {
            hour /= 10
            partial = (partial - 1).coerceAtLeast(0)
        } else if (partial == 0 && minute == 0) {
            active = TimeField.HOUR
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
            // Header
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

            // Input: боксы час:минута с подписями
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.Top,
            ) {
                TimeField(
                    value = hour,
                    label = "Часы",
                    isActive = active == TimeField.HOUR,
                    onClick = { active = TimeField.HOUR; partial = 0 },
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
                    isActive = active == TimeField.MINUTE,
                    onClick = { active = TimeField.MINUTE; partial = 0 },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions
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

            // Клавиатура — с тем же нижним отступом, что на форме добавления
            NumericKeyboard(
                onKeyClick = ::onDigit,
                onBackspace = ::onBackspace,
                backgroundColor = Color(0xFFECE6F0),
            )
        }
    }
}

/** Бокс времени (часы/минуты) в стиле M3 TimeInput с подписью снизу. */
@Composable
private fun TimeField(
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

/**
 * Логика набора цифры в бокс времени. Возвращает (новое значение, число введённых цифр,
 * завершён ли бокс — можно переходить дальше). [max] — верхняя граница (23 для часов, 59 для минут).
 */
private fun typeDigit(value: Int, partial: Int, digit: Int, max: Int): Triple<Int, Int, Boolean> {
    val tensMax = max / 10
    return when (partial) {
        0 -> {
            // Первая цифра: если как десятки она уже больше границы (напр. час «3»),
            // значение готово — сразу переходим дальше.
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
        1 -> {
            val candidate = value * 10 + digit
            if (candidate <= max) {
                Triple(candidate, 2, true)
            } else {
                // не влезло — начинаем бокс заново с этой цифры
                Triple(digit, if (digit > tensMax) 2 else 1, digit > tensMax)
            }
        }
        else -> {
            // бокс уже заполнен — начинаем заново
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
    }
}

/**
 * Пикер даты: штатный M3 [DatePicker] в [DatePickerDialog] — календарь с переключением месяцев
 * и кнопками «Отмена»/«Применить».
 */
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
private fun PickerScaffold(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(TextColor.copy(alpha = 0.4f))
                )
            }

            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            content()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RowSeparator() {
    HorizontalDivider(thickness = 0.5.dp, color = DividerColor.copy(alpha = 0.4f))
}

@Composable
private fun PickerRow(
    emoji: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (subtitle != null) 69.dp else 64.dp)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color(0xFFFEF7FF),
            border = BorderStroke(1.dp, DividerColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF49454F)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AccentColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}
