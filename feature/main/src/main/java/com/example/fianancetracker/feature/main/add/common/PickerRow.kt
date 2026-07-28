package com.example.fianancetracker.feature.main.add.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun PickerRow(
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
