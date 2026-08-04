package com.example.fianancetracker.feature.analytics.components.bottom_sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.ui.utils.accountEmoji
import com.example.fianancetracker.core.ui.utils.accountTypeTitleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    accounts: List<Account>,
    selectedAccountName: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var localSelected by remember { mutableStateOf(selectedAccountName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1D1B20).copy(alpha = 0.4f)),
                )
            }

            Text(
                text = "Счёт",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )

            AccountFilterRow(
                emoji = "💳",
                name = "Все счета",
                isSelected = localSelected == null,
                onClick = { localSelected = null },
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0xFFC4C7C5).copy(alpha = 0.4f),
            )

            accounts.forEachIndexed { index, account ->
                AccountFilterRow(
                    emoji = account.emoji.ifEmpty { accountEmoji(account.type) },
                    name = account.name,
                    subtitle = stringResource(accountTypeTitleRes(account.type)),
                    isSelected = localSelected == account.name,
                    onClick = { localSelected = account.name },
                )
                if (index < accounts.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color(0xFFC4C7C5).copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(100),
                color = Color(0xFF6750A4),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clickable { onSelect(localSelected) },
                ) {
                    Text(
                        text = "Готово",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
