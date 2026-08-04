package com.example.fianancetracker.feature.main.add.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.category.Category
import com.example.fianancetracker.core.ui.utils.accountEmoji
import com.example.fianancetracker.core.ui.utils.accountTypeTitleRes
import com.example.fianancetracker.feature.main.R
import com.example.fianancetracker.feature.main.add.common.*

enum class AddPicker { NONE, CATEGORY, ACCOUNT, DATE, TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    accounts: List<Account>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = stringResource(R.string.label_account), onDismiss = onDismiss) {
        accounts.forEachIndexed { index, account ->
            if (index != 0) RowSeparator()
            PickerRow(
                emoji = account.emoji.ifEmpty { accountEmoji(account.type) },
                name = account.name,
                subtitle = stringResource(accountTypeTitleRes(account.type)),
                isSelected = account.id == selectedId,
                onClick = { onSelect(account.id) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerScaffold(title = stringResource(R.string.label_category), onDismiss = onDismiss) {
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
