package com.example.fianancetracker.feature.main.add.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
