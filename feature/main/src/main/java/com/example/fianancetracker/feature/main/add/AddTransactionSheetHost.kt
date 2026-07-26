package com.example.fianancetracker.feature.main.add

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Хост экрана добавления: показывает основной bottom sheet формы, а поверх — пикеры
 * выбора статьи/счёта. Состоянием владеет [com.example.fianancetracker.feature.main.MainViewModel];
 * здесь только рендер и проброс колбэков.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheetHost(
    state: AddTransactionUiState,
    onDismiss: () -> Unit,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    onCategoryClick: () -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onAccountClick: () -> Unit,
    onPickCategory: (Long) -> Unit,
    onPickAccount: (String) -> Unit,
    onPickDate: (java.time.LocalDate) -> Unit,
    onPickTime: (java.time.LocalTime) -> Unit,
    onPickerDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Пока открыт любой пикер (статья/счёт/дата/время), сам sheet добавления прячем,
    // чтобы не было двух листов друг за другом. Состояние формы живёт во ViewModel и сохраняется.
    if (state.openPicker == AddPicker.NONE) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null,
            containerColor = Color.White,
            contentWindowInsets = { WindowInsets(0) },
        ) {
            AddTransactionSheetContent(
                state = state.form,
                onCategoryClick = onCategoryClick,
                onDateClick = onDateClick,
                onTimeClick = onTimeClick,
                onAccountClick = onAccountClick,
                onKeyClick = onKeyClick,
                onBackspace = onBackspace,
                onConfirm = onConfirm,
            )
        }
    }

    when (state.openPicker) {
        AddPicker.CATEGORY -> CategoryPickerSheet(
            categories = state.categories,
            selectedId = state.selectedCategoryId,
            onSelect = onPickCategory,
            onDismiss = onPickerDismiss,
        )
        AddPicker.ACCOUNT -> AccountPickerSheet(
            accounts = state.accounts,
            selectedId = state.selectedAccountId,
            onSelect = onPickAccount,
            onDismiss = onPickerDismiss,
        )
        AddPicker.DATE -> DatePickerDialogM3(
            initial = state.date,
            onConfirm = onPickDate,
            onDismiss = onPickerDismiss,
        )
        AddPicker.TIME -> TimePickerSheet(
            initial = state.time,
            onConfirm = onPickTime,
            onDismiss = onPickerDismiss,
        )
        AddPicker.NONE -> Unit
    }
}
