package com.example.fianancetracker.feature.main.add.account
import com.example.fianancetracker.feature.main.add.common.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun EmojiSelector(
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ACCOUNT_EMOJIS.forEach { emoji ->
            val isSelected = emoji == selected
            Surface(
                onClick = { onSelect(emoji) },
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) TimeActiveBg else Color(0xFFFEF7FF),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) AccentColor else DividerColor,
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }
        }
    }
}
