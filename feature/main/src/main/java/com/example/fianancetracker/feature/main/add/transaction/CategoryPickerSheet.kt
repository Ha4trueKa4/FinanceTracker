package com.example.fianancetracker.feature.main.add.transaction
import com.example.fianancetracker.feature.main.add.common.*

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.fianancetracker.core.domain.category.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = "Статья", onDismiss = onDismiss) {
        categories.forEachIndexed { index, category ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = category.emoji,
                name = category.name,
                isSelected = category.id == selectedId,
                onClick = { onSelect(category.id) },
            )
        }
    }
}
