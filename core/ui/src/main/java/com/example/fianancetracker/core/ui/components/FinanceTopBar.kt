package com.example.fianancetracker.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTopBar(
    dateText: String,
    onCalendarClick: () -> Unit = {},
    actionIcon1: ImageVector = ImageVector.vectorResource(R.drawable.analytics),
    actionContentDescription1: String = "Аналитика",
    onAction1Click: () -> Unit = {},
    actionIcon2: ImageVector = Icons.Filled.Tune,
    actionContentDescription2: String = "Фильтр",
    onAction2Click: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Surface(
                shape = RoundedCornerShape(100),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.clickable { onCalendarClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Календарь",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onAction1Click) {
                Icon(
                    imageVector = actionIcon1,
                    contentDescription = actionContentDescription1,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onAction2Click) {
                Icon(
                    imageVector = actionIcon2,
                    contentDescription = actionContentDescription2,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
