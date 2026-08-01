package com.example.fianancetracker.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BorderColor = Color(0xFFC4C7C5)
private val LabelColor = Color(0xFF1D1B20)
private val ErrorColor = Color(0xFFB3261E)

data class FilterRowStyle(
    val height: Dp = 56.dp,
    val iconSize: Dp = 32.dp,
    val iconBackground: Color = Color.Transparent,
    val iconBorderWidth: Dp = 1.dp,
    val iconTint: Color = Color(0xFF49454F),
    val badgeTextColor: Color = Color(0xFF49454F),
    val badgeVerticalPadding: Dp = 4.dp,
)

object FilterRowDefaults {
    val Default = FilterRowStyle()

    val Sheet = FilterRowStyle(
        height = 64.dp,
        iconSize = 40.dp,
        iconBackground = Color(0xFFFEF7FF),
        iconBorderWidth = 0.75.dp,
        iconTint = Color(0xFF7B5EA7),
        badgeTextColor = LabelColor,
        badgeVerticalPadding = 6.dp,
    )
}

@Composable
fun FilterRow(
    icon: Painter,
    label: String,
    badgeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: FilterRowStyle = FilterRowDefaults.Default,
    isError: Boolean = false,
) {
    val badgeBorderColor = if (isError) ErrorColor else BorderColor
    val badgeTextColor = if (isError) ErrorColor else style.badgeTextColor
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(style.height)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(style.iconSize),
            shape = CircleShape,
            color = style.iconBackground,
            border = BorderStroke(style.iconBorderWidth, BorderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = style.iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = LabelColor,
            modifier = Modifier.weight(1f)
        )

        if (badgeText.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(100),
                border = BorderStroke(1.dp, badgeBorderColor),
                color = Color.Transparent,
                modifier = Modifier.widthIn(max = 190.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = badgeTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = style.badgeVerticalPadding
                    )
                )
            }
        }
    }
}
