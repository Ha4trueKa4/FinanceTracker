package com.example.fianancetracker.feature.main.add.common

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.feature.main.R

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
internal fun Key(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(KeyHeight),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (key == KEY_BACKSPACE) {
                Icon(
                    painter = painterResource(R.drawable.ic_backspace),
                    contentDescription = stringResource(R.string.cd_delete),
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
