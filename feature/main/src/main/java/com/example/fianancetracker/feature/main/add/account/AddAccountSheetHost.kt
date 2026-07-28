package com.example.fianancetracker.feature.main.add.account

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountSheetHost(
    state: AddAccountUiState,
    onDismiss: () -> Unit,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmojiSelect: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    onPickCurrency: (String) -> Unit,
    onCurrencyPickerDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        contentWindowInsets = { WindowInsets(0) },
    ) {
        AddAccountSheetContent(
            state = state.form,
            onKeyClick = onKeyClick,
            onBackspace = onBackspace,
            onConfirm = onConfirm,
            onNameChange = onNameChange,
            onEmojiSelect = onEmojiSelect,
            onCurrencyClick = onCurrencyClick,
        )
    }

    if (state.showCurrencyPicker) {
        CurrencyPickerSheet(
            selectedCode = state.currency,
            onSelect = onPickCurrency,
            onDismiss = onCurrencyPickerDismiss,
        )
    }
}
