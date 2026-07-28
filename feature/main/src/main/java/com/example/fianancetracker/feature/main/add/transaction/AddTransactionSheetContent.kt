package com.example.fianancetracker.feature.main.add.transaction
import com.example.fianancetracker.feature.main.add.common.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.core.ui.components.FilterRow
import com.example.fianancetracker.core.ui.components.FilterRowDefaults
import com.example.fianancetracker.feature.main.R

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

        FilterRow(painterResource(R.drawable.ic_form_category), "Статья", state.category, onCategoryClick, style = FilterRowDefaults.Sheet, isError = state.categoryError)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), "Дата", state.dateText, onDateClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_date), "Время", state.timeText, onTimeClick, style = FilterRowDefaults.Sheet)
        RowDivider()
        FilterRow(painterResource(R.drawable.ic_form_account), "Счёт", state.account, onAccountClick, style = FilterRowDefaults.Sheet, isError = state.accountError)
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
