package com.example.fianancetracker.feature.main.add

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.ui.components.FilterRow
import com.example.fianancetracker.core.ui.components.FilterRowDefaults
import com.example.fianancetracker.feature.main.R

private val AmountColor = Color(0xFF1C1B1F)
private val CursorColor = Color(0xFFB69DF8)
private val LineColor = Color(0xFFC4C7C5)
private val ErrorColor = Color(0xFFB3261E)
private val FabColor = Color(0xFF1C1B1F)
private val KeyboardBg = Color(0xFFE6E0E9)

/** Высота клавиши — она же нижний отступ клавиатуры от края экрана (по макету). */
private val KeyHeight = 56.dp

/** Спец-клавиши цифровой клавиатуры. */
const val KEY_BACKSPACE = "⌫"

/**
 * Контент bottom sheet «Добавление дохода/расхода» по макету Figma.
 * «Тупой» composable: принимает [AddTransactionFormState] + колбэки, только рисует.
 */
@Composable
fun AddTransactionSheetContent(
    state: AddTransactionFormState,
    modifier: Modifier = Modifier,
    onCategoryClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onKeyClick: (String) -> Unit = {},
    onBackspace: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShapeTop())
            .background(Color.White)
    ) {
        Handle()

        Spacer(modifier = Modifier.height(24.dp))

        AmountArea(amountText = state.amountText, isError = state.amountError)

        Spacer(modifier = Modifier.height(16.dp))

        FilterRow(painterResource(R.drawable.ic_form_category), "Статья", state.category, onCategoryClick, style = FilterRowDefaults.Sheet, isError = state.categoryError)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), "Дата", state.dateText, onDateClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), "Время", state.timeText, onTimeClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_account), "Счёт", state.account, onAccountClick, style = FilterRowDefaults.Sheet, isError = state.accountError)
        RowDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            ConfirmFab(
                onClick = onConfirm,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        NumericKeyboard(onKeyClick = onKeyClick, onBackspace = onBackspace)
    }
}

private fun RoundedCornerShapeTop() =
    androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

@Composable
private fun Handle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                .background(Color(0xFFCAC4D0))
        )
    }
}

@Composable
private fun AmountArea(amountText: String, isError: Boolean = false) {
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

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(LineColor.copy(alpha = 0.5f))
    )
}

@Composable
private fun ConfirmFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
        color = FabColor,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Готово",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Цифровая клавиатура «1–9, 0, ⌫». Переиспользуется на форме добавления (ввод суммы)
 * и в пикере времени (ввод часов/минут). [backgroundColor] и [bottomPadding] позволяют
 * подогнать её под конкретный экран (на форме — крупный нижний отступ, в пикере — вплотную к краю).
 */
@Composable
internal fun NumericKeyboard(
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    backgroundColor: Color = KeyboardBg,
    bottomPadding: Dp = KeyHeight,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", KEY_BACKSPACE),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .navigationBarsPadding()
            .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Key(
                            key = key,
                            modifier = Modifier.weight(1f),
                            onClick = { if (key == KEY_BACKSPACE) onBackspace() else onKeyClick(key) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Key(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(KeyHeight),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (key == KEY_BACKSPACE) {
                Icon(
                    painter = painterResource(R.drawable.ic_backspace),
                    contentDescription = "Удалить",
                    tint = AmountColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = key,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = AmountColor
                )
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 720)
@Composable
private fun AddTransactionSheetPreview() {
    AddTransactionSheetContent(
        state = AddTransactionFormState(
            amountText = "214 ₽",
            category = "Ремонт",
            dateText = "20 февраля",
            timeText = "23:42",
            account = "Сбер",
        )
    )
}
