package com.example.fianancetracker.feature.main.add.account
import com.example.fianancetracker.feature.main.add.common.*

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerSheet(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = "Валюта", onDismiss = onDismiss) {
        Currency.entries.forEachIndexed { index, currency ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = currency.symbol,
                name = currency.title,
                isSelected = currency.code == selectedCode,
                onClick = { onSelect(currency.code) },
            )
        }
    }
}
