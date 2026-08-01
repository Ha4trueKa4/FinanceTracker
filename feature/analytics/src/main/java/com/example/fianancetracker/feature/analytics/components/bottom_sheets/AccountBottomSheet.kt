package com.example.fianancetracker.feature.analytics.components.bottom_sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.ui.utils.accountEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    accounts: List<Account>,
    selectedAccountName: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var localSelected by remember { mutableStateOf(selectedAccountName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1D1B20).copy(alpha = 0.4f))
                )
            }

            Text(
                text = "Счёт",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            AccountFilterRow(
                emoji = "💳",
                name = "Все счета",
                isSelected = localSelected == null,
                onClick = { localSelected = null }
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0xFFC4C7C5).copy(alpha = 0.4f)
            )

            accounts.forEachIndexed { index, account ->
                AccountFilterRow(
                    emoji = account.emoji.ifEmpty { accountEmoji(account.type) },
                    name = account.name,
                    subtitle = account.type.displayName,
                    isSelected = localSelected == account.name,
                    onClick = { localSelected = account.name }
                )
                if (index < accounts.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color(0xFFC4C7C5).copy(alpha = 0.4f)
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
                color = Color(0xFF6750A4)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clickable { onSelect(localSelected) }
                ) {
                    Text(
                        text = "Готово",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AccountFilterRow(
    emoji: String,
    name: String,
    subtitle: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (subtitle != null) 69.dp else 64.dp)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color(0xFFFEF7FF),
            border = BorderStroke(1.dp, Color(0xFFC4C7C5))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = emoji,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1B20)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF49454F)
                )
            }
        }

        val checkSize = 20.dp
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF7B5EA7),
                modifier = Modifier.size(checkSize)
            )
        } else {
            Spacer(modifier = Modifier.size(checkSize))
        }
    }
}
