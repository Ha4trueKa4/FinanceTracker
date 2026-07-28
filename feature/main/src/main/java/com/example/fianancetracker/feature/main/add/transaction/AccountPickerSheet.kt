package com.example.fianancetracker.feature.main.add.transaction
import com.example.fianancetracker.feature.main.add.common.*

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.ui.utils.accountEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    accounts: List<Account>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = "Счёт", onDismiss = onDismiss) {
        accounts.forEachIndexed { index, account ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = account.emoji.ifEmpty { accountEmoji(account.type) },
                name = account.name,
                subtitle = account.type.displayName,
                isSelected = account.id == selectedId,
                onClick = { onSelect(account.id) },
            )
        }
    }
}
