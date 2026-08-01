package com.example.fianancetracker.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.core.ui.model.ListItemModel

@Composable
fun FinanceListContent(
    items: List<ListItemModel>,
    modifier: Modifier = Modifier,
    showDividers: Boolean = false,
    onItemClick: (String) -> Unit = {},
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет операций",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                if (showDividers) {
                    FinanceListItem(
                        item = item,
                        modifier = Modifier.padding(horizontal = 0.dp),
                        onClick = { onItemClick(item.id) }
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                } else {
                    FinanceListItem(item = item, onClick = { onItemClick(item.id) })
                }
            }
        }
    }
}
