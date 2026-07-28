package com.example.fianancetracker.feature.analytics.components.bottom_sheets

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.fianancetracker.core.domain.analytics.AnalyticsFilter
import com.example.fianancetracker.core.domain.analytics.Period

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodBottomSheet(
    filter: AnalyticsFilter,
    customRangeLabel: String,
    onApply: (AnalyticsFilter) -> Unit,
    onDismiss: () -> Unit,
    onCustomSelected: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var localPeriod by remember { mutableStateOf(filter.period) }

    val options = listOf(
        Period.CUSTOM to "Произвольный",
        Period.WEEK to "За неделю",
        Period.MONTH to "Месяц",
        Period.QUARTER to "Квартал",
        Period.YEAR to "Год"
    )

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
                .verticalScroll(rememberScrollState())
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
                text = "Период",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            options.forEachIndexed { index, (period, label) ->
                val isCustom = period == Period.CUSTOM
                val rowHeight = if (isCustom) 69.dp else 51.dp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .clickable {
                            if (isCustom) {
                                onCustomSelected()
                            } else {
                                localPeriod = period
                            }
                        }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1D1B20)
                        )
                        if (isCustom) {
                            Text(
                                text = customRangeLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF49454F)
                            )
                        }
                    }

                    if (!isCustom) {
                        val checkSize = 20.dp
                        if (period == localPeriod) {
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

                if (index < options.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
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
                    modifier = Modifier.clickable {
                        onApply(filter.copy(period = localPeriod))
                    }
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
