package com.example.fianancetracker.feature.main.add.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.ui.components.FilterRow
import com.example.fianancetracker.core.ui.components.FilterRowDefaults
import com.example.fianancetracker.core.ui.components.NumericKeyboard
import com.example.fianancetracker.feature.main.R
import com.example.fianancetracker.feature.main.add.common.*

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

@Composable
fun AddAccountSheetContent(
    state: AddAccountFormState,
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit = {},
    onBackspace: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onEmojiSelect: (String) -> Unit = {},
    onCurrencyClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .imePadding()
    ) {
        Handle()

        Spacer(modifier = Modifier.height(24.dp))

        AmountArea(amountText = state.amountText, isError = state.amountError)

        Spacer(modifier = Modifier.height(16.dp))

        NameField(value = state.name, onValueChange = onNameChange, isError = state.nameError)
        RowDivider()

        val currencyName = Currency.entries.firstOrNull { it.code == state.currency }
            ?.let { stringResource(it.titleRes) } ?: state.currency
        FilterRow(
            painterResource(R.drawable.ic_form_account),
            stringResource(R.string.label_currency),
            currencyName,
            onCurrencyClick,
            style = FilterRowDefaults.Sheet,
        )
        RowDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.account_icon),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF49454F),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmojiSelector(selected = state.emoji, onSelect = onEmojiSelect)

        Spacer(modifier = Modifier.weight(1f))

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
