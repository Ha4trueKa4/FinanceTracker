package com.example.fianancetracker.feature.main.add.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.feature.main.add.common.*

@Composable
internal fun CommentField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = AmountColor),
        cursorBrush = SolidColor(CursorColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = "Комментарий",
                        fontSize = 16.sp,
                        color = if (isError) ErrorColor else LineColor,
                    )
                }
                inner()
            }
        }
    )
}

internal fun typeDigit(value: Int, partial: Int, digit: Int, max: Int): Triple<Int, Int, Boolean> {
    val tensMax = max / 10
    return when (partial) {
        0 -> {
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
        1 -> {
            val candidate = value * 10 + digit
            if (candidate <= max) {
                Triple(candidate, 2, true)
            } else {
                Triple(digit, if (digit > tensMax) 2 else 1, digit > tensMax)
            }
        }
        else -> {
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
    }
}
