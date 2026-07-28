package com.example.fianancetracker.feature.main.add.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.feature.main.R

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
