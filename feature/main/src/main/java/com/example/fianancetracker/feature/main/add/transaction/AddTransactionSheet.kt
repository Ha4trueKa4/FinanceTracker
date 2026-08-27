package com.example.fianancetracker.feature.main.add.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.core.ui.components.FilterRow
import com.example.fianancetracker.core.ui.components.FilterRowDefaults
import com.example.fianancetracker.core.ui.components.NumericKeyboard
import com.example.fianancetracker.feature.main.R
import com.example.fianancetracker.feature.main.add.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheetHost(
    state: AddTransactionUiState,
    onDismiss: () -> Unit,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    onCommentChange: (String) -> Unit,
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
            onCommentChange = onCommentChange,
        )
    }

    when (state.openPicker) {
        AddPicker.CATEGORY -> CategoryPickerSheet(
            categories = state.visibleCategories,
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

@Composable
fun AddTransactionSheetContent(
    state: AddTransactionFormState,
    modifier: Modifier = Modifier,
    onCategoryClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onKeyClick: (String) -> Unit = {},
    onBackspace: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCommentChange: (String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
    ) {
        Handle()

        Spacer(modifier = Modifier.height(24.dp))

        AmountArea(amountText = state.amountText, isError = state.amountError)

        Spacer(modifier = Modifier.height(16.dp))

        val notSelected = stringResource(R.string.not_selected)
        FilterRow(painterResource(R.drawable.ic_form_category), stringResource(R.string.label_category), state.category.ifEmpty { notSelected }, onCategoryClick, style = FilterRowDefaults.Sheet, isError = state.categoryError)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), stringResource(R.string.label_date), state.dateText, onDateClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), stringResource(R.string.label_time), state.timeText, onTimeClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_account), stringResource(R.string.label_account), state.account.ifEmpty { notSelected }, onAccountClick, style = FilterRowDefaults.Sheet, isError = state.accountError)
        RowDivider()
        CommentField(value = state.comment, onValueChange = onCommentChange, isError = state.commentError)
        RowDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            ConfirmFab(
                onClick = onConfirm,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        NumericKeyboard(onKeyClick = onKeyClick, onBackspace = onBackspace)
    }
}
