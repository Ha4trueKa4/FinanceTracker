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
import androidx.compose.material3.MaterialTheme
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

data class FilterRowStyle(
    val height: Dp = 56.dp,
    val iconSize: Dp = 32.dp,
    val iconBackground: Color,
    val iconBorderWidth: Dp = 1.dp,
    val iconTint: Color,
    val badgeTextColor: Color,
    val badgeVerticalPadding: Dp = 4.dp,
)

object FilterRowDefaults {
    val Default: FilterRowStyle
        @Composable get() = FilterRowStyle(
            iconBackground = Color.Transparent,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )

    val Sheet: FilterRowStyle
        @Composable get() = FilterRowStyle(
            height = 64.dp,
            iconSize = 40.dp,
            iconBackground = MaterialTheme.colorScheme.surfaceContainerHighest,
            iconBorderWidth = 0.75.dp,
            iconTint = MaterialTheme.colorScheme.primary,
            badgeTextColor = MaterialTheme.colorScheme.onSurface,
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
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val errorColor = MaterialTheme.colorScheme.error
    val badgeBorderColor = if (isError) errorColor else borderColor
    val badgeTextColor = if (isError) errorColor else style.badgeTextColor
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
            border = BorderStroke(style.iconBorderWidth, borderColor)
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
            color = MaterialTheme.colorScheme.onSurface,
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
